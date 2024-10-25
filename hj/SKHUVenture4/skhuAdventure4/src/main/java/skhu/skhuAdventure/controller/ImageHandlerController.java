package skhu.skhuAdventure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import skhu.skhuAdventure.service.ImageHandlerService;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageHandlerController {

    private final ImageHandlerService imageHandlerService;

    @Autowired
    public ImageHandlerController(ImageHandlerService imageHandlerService) {
        this.imageHandlerService = imageHandlerService;
    }

    // 기존 코드
//    @GetMapping
//    public ResponseEntity<List<String>> getImageUrls(@RequestParam("userId") String userId) {
//        List<String> imageUrls = imageHandlerService.getImageUrlsByUserId(userId);
//        return ResponseEntity.ok(imageUrls);
//    }

    
    // 새 변형 코드
    @GetMapping
    public ResponseEntity<?> getImageUrls(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }

        List<String> imageUrls = imageHandlerService.getImageUrlsByUserId(userId);
        return ResponseEntity.ok(imageUrls);
    }


}