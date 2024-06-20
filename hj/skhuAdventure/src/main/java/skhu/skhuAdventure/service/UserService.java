package skhu.skhuAdventure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import skhu.skhuAdventure.entity.UserEntity;
import skhu.skhuAdventure.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity authenticate(String email, String password) {
        System.out.println("Authenticating user with email: " + email);  // 디버깅 로그 추가
        UserEntity user = userRepository.findByEmail(email);
        if (user != null && user.getPw().equals(password)) {
            System.out.println("User authenticated: " + email);  // 디버깅 로그 추가
            return user;
        }
        System.out.println("Authentication failed for user: " + email);  // 디버깅 로그 추가
        return null;
    }

    public void createUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }
}
