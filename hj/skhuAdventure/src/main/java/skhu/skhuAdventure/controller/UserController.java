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

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Invalid input data", HttpStatus.BAD_REQUEST);
        }

        // 디버깅 로그 추가
        System.out.println("Signup endpoint called");
        if (bindingResult.hasErrors()) {
            // 검증 오류 로그 추가
            System.out.println("Validation errors: " + bindingResult.getAllErrors());
            return new ResponseEntity<>("Invalid input data", HttpStatus.BAD_REQUEST);
        }


        UserEntity userEntity = UserEntity.builder()
                .name(user.getName())
                .email(user.getEmail())
                .pw(user.getPw())
                .reward(user.getReward())
                .build();
        userService.createUser(userEntity);

        // 성공 로그 추가
        System.out.println("User registered successfully");

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        boolean isAuthenticated = userService.authenticate(user.getEmail(), user.getPw());

        // 디버깅 로그 추가
        System.out.println("Login endpoint called");

        if (isAuthenticated) {
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
}
