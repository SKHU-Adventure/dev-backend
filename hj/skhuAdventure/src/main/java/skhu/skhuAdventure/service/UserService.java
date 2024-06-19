package skhu.skhuAdventure.service;

import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import skhu.skhuAdventure.repository.UserRepository;
import skhu.skhuAdventure.entity.UserEntity;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void createUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    // 로그인 부분을 담당하는 anthenticate 메소드.
    public boolean authenticate(String email, String pw) {
        UserEntity user = userRepository.findByEmail(email);
        if (user != null && user.getPw().equals(pw)) {
            return true;
        }
        return false;
    }


}
