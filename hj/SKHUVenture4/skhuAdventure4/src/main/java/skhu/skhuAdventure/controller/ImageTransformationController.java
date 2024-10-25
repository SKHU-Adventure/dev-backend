package skhu.skhuAdventure.controller;

import org.springframework.http.HttpStatus;
import skhu.skhuAdventure.service.ImageTransformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

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
    public ResponseEntity<String> transformImage(@RequestParam("image") MultipartFile image, HttpSession session) {
        try {
            String userId = (String) session.getAttribute("userId");
            if (userId == null) {
                return new ResponseEntity<>("User not logged in", HttpStatus.UNAUTHORIZED);
            }

            String imageUrl = imageTransformationService.transformImage(userId, image);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Image transformation failed: " + e.getMessage());
        }
    }
}