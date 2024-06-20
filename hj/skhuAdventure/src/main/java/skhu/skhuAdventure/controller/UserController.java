package skhu.skhuAdventure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import skhu.skhuAdventure.dto.User;
import skhu.skhuAdventure.entity.UserEntity;
import skhu.skhuAdventure.service.UserService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody User user, BindingResult bindingResult) {
        System.out.println("Signup endpoint called");  // 디버깅 로그 추가
        if (bindingResult.hasErrors()) {
            System.out.println("Validation errors: " + bindingResult.getAllErrors());  // 디버깅 로그 추가
            return new ResponseEntity<>("Invalid input data", HttpStatus.BAD_REQUEST);
        }

        try {
            UserEntity userEntity = UserEntity.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .pw(user.getPw())
                    .reward(user.getReward())
                    .build();
            userService.createUser(userEntity);
            System.out.println("User registered successfully: " + user.getEmail());  // 디버깅 로그 추가
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("Error during user registration: " + e.getMessage());  // 디버깅 로그 추가
            return new ResponseEntity<>("User registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        System.out.println("Login endpoint called with email: " + user.getEmail());  // 디버깅 로그 추가
        try {
            UserEntity authenticatedUser = userService.authenticate(user.getEmail(), user.getPw());

            if (authenticatedUser != null) {
                Map<String, String> response = new HashMap<>();
                response.put("userId", String.valueOf(authenticatedUser.getId()));
                response.put("message", "Login successful");
                System.out.println("Login successful for user: " + user.getEmail());  // 디버깅 로그 추가
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                System.out.println("Invalid email or password for user: " + user.getEmail());  // 디버깅 로그 추가
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid email or password");
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());  // 디버깅 로그 추가
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Login failed");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
