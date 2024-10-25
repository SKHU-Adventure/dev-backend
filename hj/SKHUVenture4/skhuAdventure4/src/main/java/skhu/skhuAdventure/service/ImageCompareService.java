package skhu.skhuAdventure.service;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.io.IOException;

@Service
public class ImageCompareService {

    @Value("${fast.server.url}")
    private String fastServerUrl;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MissionService missionService;

    private static final String COMPARE_IMAGES_ENDPOINT = "/model/compare/";
    private static final String TRANSFORM_IMAGE_ENDPOINT = "/model/transform/";


    public ImageCompareService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public int processImageComparison(MultipartFile image, String buildingNumber, String userId) throws IOException {
        // 1. 이미지 비교 API 호출
        int comparisonResult = compareImage(image, buildingNumber);

        // 2. 비교 결과가 성공(1)인 경우 미션 완료 처리
        if (comparisonResult == 1) {
            try {
                missionService.completeMission(userId, Integer.parseInt(buildingNumber));
                // 3. 이미지 변환 API 호출
                transformImage(image, buildingNumber);
            } catch (RuntimeException e) {
//                throw new IOException("Failed to complete mission: " + e.getMessage());
                return 0; // 미션 실패
            }
        }

        return comparisonResult;

    }


    public int compareImage(MultipartFile image, String buildingNumber) throws IOException {
        String url = fastServerUrl + COMPARE_IMAGES_ENDPOINT + "?building_number=" + buildingNumber;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new MultipartInputStreamFileResource(image.getInputStream(), image.getOriginalFilename()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Integer> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Integer.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new IOException("Failed to compare image: " + response.getStatusCode());
        }
    }

    
    // 새로 작성
    private void transformImage(MultipartFile image, String buildingNumber) throws IOException {
        String url = fastServerUrl + TRANSFORM_IMAGE_ENDPOINT + "?building_number=" + buildingNumber;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new MultipartInputStreamFileResource(image.getInputStream(), image.getOriginalFilename()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IOException("Failed to transform image: " + response.getStatusCode());
        }
    }

}