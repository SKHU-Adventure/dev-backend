package skhu.skhuAdventure.controller;

import skhu.skhuAdventure.service.ImageTransformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageTransformationController {

    private final ImageTransformationService imageTransformationService;

    @Autowired
    public ImageTransformationController(ImageTransformationService imageTransformationService) {
        this.imageTransformationService = imageTransformationService;
    }

    @PostMapping("/transform")
    public ResponseEntity<String> transformImage(@RequestParam("userId") int userId,
                                                 @RequestParam("image") MultipartFile image) {
        try {
            String imageUrl = imageTransformationService.transformImage(userId, image);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Image transformation failed: " + e.getMessage());
        }
    }
}
