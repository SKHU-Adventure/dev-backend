package net.skhu.service;


import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.skhu.repository.UserRepository;
import net.skhu.entity.UserEntity;

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



//    public void updateName(String email, String name) {
//        UserEntity userEntity = userRepository.findByEmail(email);
//        if (userEntity != null) {
//            userEntity.setName(name);
//            userRepository.save(userEntity);
//        }
//    }

//    public void changePassword(String email, String newPassword) {
//        UserEntity userEntity = userRepository.findByEmail(email);
//        if (userEntity != null) {
//            userEntity.setPw(newPassword);
//            userRepository.save(userEntity);
//        }
//    }

}
