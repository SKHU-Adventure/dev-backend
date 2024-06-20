package skhu.skhuAdventure.controller;

import skhu.skhuAdventure.service.ImageCompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageCompareController {

    @Autowired
    private ImageCompareService imageCompareService;

    @PostMapping("/compare")
    public ResponseEntity<Integer> compareImage(@RequestParam("image") MultipartFile image,
                                                @RequestParam("buildingNumber") String buildingNumber) throws IOException {
        int result = imageCompareService.compareImage(image, buildingNumber);
        return ResponseEntity.ok(result);
    }
}
