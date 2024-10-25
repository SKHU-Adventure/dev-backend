package skhu.skhuAdventure.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skhu.skhuAdventure.Dto.Users;
import skhu.skhuAdventure.entity.MajorEntity;
import skhu.skhuAdventure.service.UsersService;
import skhu.skhuAdventure.service.MissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private MissionService missionService;

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody Users users) {
        try {
            ResponseEntity<String> response = usersService.createUser(users);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                // 회원가입 성공 시 미션 초기화
                missionService.initializeMissionsForUser(users.getUserId());
                return new ResponseEntity<>("User registered and missions initialized", HttpStatus.CREATED);
            }
            return response;
        } catch (Exception e) {
            logger.error("Error during signup process: ", e);
            return new ResponseEntity<>("Error during signup: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Users users, HttpSession session) {
        boolean isAuthenticated = usersService.authenticate(users.getUserId(), users.getPassword());

        if (isAuthenticated) {
            session.setAttribute("userId", users.getUserId());
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid student ID or password", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().body("User not logged in");
        }
        try {
            Users userInfo = usersService.getUserInfo(userId);
            return ResponseEntity.ok(userInfo);
        } catch (RuntimeException e) {
            logger.error("Error fetching user info for user {}: ", userId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // MajorEntity를 가져오는 메서드
    private MajorEntity getMajorEntity(Integer majorId) {
        return usersService.getMajorById(majorId);
    }
}