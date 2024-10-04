package skhu.skhuAdventure.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skhu.skhuAdventure.entity.MissionEntity;
import skhu.skhuAdventure.service.MissionService;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    private static final Logger logger = LoggerFactory.getLogger(MissionController.class);

    @Autowired
    private MissionService missionService;

    @PostMapping("/initialize/{userId}")
    public ResponseEntity<String> initializeMissions(@PathVariable String userId) {
        try {
            missionService.initializeMissionsForUser(userId);
            return ResponseEntity.ok("Missions initialized for user: " + userId);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/building/{buildingId}")
    public ResponseEntity<String> completeMission(
            @PathVariable Integer buildingId,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().body("User not logged in");
        }
        try {
            missionService.completeMission(userId, buildingId);
            return ResponseEntity.ok("Mission completed successfully");
        } catch (RuntimeException e) {
            logger.error("Error completing mission: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<MissionEntity>> getUserMissions(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            List<MissionEntity> missions = missionService.getUserMissions(userId);
            return ResponseEntity.ok(missions);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}