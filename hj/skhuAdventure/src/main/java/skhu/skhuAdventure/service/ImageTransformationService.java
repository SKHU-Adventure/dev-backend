package skhu.skhuAdventure.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import skhu.skhuAdventure.entity.ImageEntity; // ImageEntity 클래스 임포트
import skhu.skhuAdventure.repository.ImageRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ImageTransformationService {

    @Value("${fast.server.url}")
    private String fastServerUrl;
    private final RestTemplate restTemplate;
    private final ImageRepository imageRepository;

    private static final String TRANSFORM_IMAGE_ENDPOINT = "/model/transform/";

    public ImageTransformationService(RestTemplate restTemplate, ImageRepository imageRepository) {
        this.restTemplate = restTemplate;
        this.imageRepository = imageRepository;
    }

    public String transformImage(int userId, MultipartFile image) throws IOException {
        String url = fastServerUrl + TRANSFORM_IMAGE_ENDPOINT;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(image.getInputStream(), image.getOriginalFilename()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<TransformResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, TransformResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return Optional.ofNullable(response.getBody().getOutput())
                    .filter(output -> !output.isEmpty())
                    .map(output -> {
                        try {
                            String imageUrl = output.get(0);
                            int imageNumber = getNextImageNumber(userId);  // 사용자별 이미지 번호 생성
                            saveImageToDatabase(userId, imageNumber, imageUrl);
                            return imageUrl;
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to process image URL", e);
                        }
                    })
                    .orElseThrow(() -> new IOException("Failed to transform image: output is empty or null"));
        } else {
            throw new IOException("Failed to transform image: " + response.getStatusCode());
        }
    }

    private int getNextImageNumber(int userId) {
        return imageRepository.countByUserId(userId) + 1;  // 사용자별 이미지 번호 증가
    }

    private void saveImageToDatabase(int userId, int imageNumber, String imageUri) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setUserId(userId);
        imageEntity.setImageNumber(imageNumber);
        imageEntity.setImageUri(imageUri);
        imageRepository.save(imageEntity);
    }

    @Getter
    private static class TransformResponse {
        @JsonProperty("output")
        private List<String> output;
    }
}
