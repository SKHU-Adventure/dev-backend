package skhu.skhuAdventure.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skhu.skhuAdventure.service.MajorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/major")
public class MajorController {

    private static final Logger logger = LoggerFactory.getLogger(MajorController.class);

    @Autowired
    private MajorService majorService;

    @GetMapping("/info")
    public ResponseEntity<String> getMajorInfo(HttpSession session) {
        String userId = (String) session.getAttribute("userId");

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body("User not logged in");
        }

        try {
            String majorName = majorService.getMajorNameByUserId(userId);
            return ResponseEntity.ok(majorName);
        } catch (RuntimeException e) {
            logger.error("Error fetching major info for user {}: ", userId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}