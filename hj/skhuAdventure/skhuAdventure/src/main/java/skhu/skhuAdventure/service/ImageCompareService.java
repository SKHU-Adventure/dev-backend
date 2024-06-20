package skhu.skhuAdventure.service;

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
    private final RestTemplate restTemplate;

    private static final String COMPARE_IMAGES_ENDPOINT = "/model/compare/";

    public ImageCompareService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
}
