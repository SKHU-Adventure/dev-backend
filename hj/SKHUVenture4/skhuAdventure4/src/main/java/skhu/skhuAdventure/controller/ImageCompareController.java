package skhu.skhuAdventure.controller;

import skhu.skhuAdventure.service.ImageCompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;


import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageCompareController {

    @Autowired
    private ImageCompareService imageCompareService;

    @PostMapping("/compare")
    public ResponseEntity<Integer> compareImage(@RequestParam("image") MultipartFile image,
                                                @RequestParam("buildingNumber") String buildingNumber,HttpSession session) throws IOException {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().body(-1); // -1은 로그인되지 않은 상태를 나타냅니다.

        }

        int result = imageCompareService.processImageComparison(image, buildingNumber, userId); // 수정본

        return ResponseEntity.ok(result);
    }
}