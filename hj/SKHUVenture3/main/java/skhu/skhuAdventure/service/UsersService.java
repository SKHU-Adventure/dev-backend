package skhu.skhuAdventure.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import skhu.skhuAdventure.Dto.Users;
import skhu.skhuAdventure.repository.MajorRepository;
import skhu.skhuAdventure.repository.UsersRepository;
import skhu.skhuAdventure.entity.UsersEntity;
import skhu.skhuAdventure.entity.MajorEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UsersService {
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MajorRepository majorRepository;

    public MajorEntity getMajorById(Integer majorId) {
        return majorRepository.findById(majorId)
                .orElseThrow(() -> {
                    logger.error("Major not found with id: {}", majorId);
                    return new RuntimeException("Major not found");
                });
    }

    @Transactional
    public ResponseEntity<String> createUser(Users users) {
        try {
            // 유효성 검사
            if (users.getUserName() == null || users.getUserId() == null || users.getPassword() == null) {
                logger.warn("Invalid input data for user creation : UserName, UserId, password 중에 빈 항목이 있음.");
                return new ResponseEntity<>("Invalid input data", HttpStatus.BAD_REQUEST);
            }

            // 사용자 ID 중복 체크
            if (usersRepository.findByUserId(users.getUserId()) != null) {
                logger.warn("User ID already exists: {}", users.getUserId());
                return new ResponseEntity<>("User ID already exists", HttpStatus.CONFLICT);
            }

            // Major 엔티티 조회
            MajorEntity majorEntity = getMajorById(users.getMajorId());
            UsersEntity usersEntity = UsersEntity.builder()
                    .userId(users.getUserId())
                    .studentId(users.getStudentId())
                    .password(users.getPassword())
                    .userName(users.getUserName())
                    .major(majorEntity)
                    .build();

            UsersEntity savedUser = usersRepository.save(usersEntity);
            logger.info("User saved successfully: {}", savedUser.getUserId());

            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating user: ", e);
            return new ResponseEntity<>("Error creating user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> authenticateUser(Users users) {
        if (authenticate(users.getUserId(), users.getPassword())) {
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid user ID or password", HttpStatus.UNAUTHORIZED);
    }

    public Users getUserInfo(String userId) {
        UsersEntity userEntity = usersRepository.findByUserId(userId);
        if (userEntity == null) {
            logger.error("User not found: {}", userId);
            throw new RuntimeException("User not found");
        }

        Users userInfo = new Users();
        userInfo.setStudentId(userEntity.getStudentId());
        userInfo.setUserId(userEntity.getUserId());
        userInfo.setUserName(userEntity.getUserName());

        return userInfo;
    }


    public boolean authenticate(String userId, String password) {
        UsersEntity users = usersRepository.findByUserId(userId);
        if (users != null && users.getPassword().equals(password)) {
            return true;
        }
        return false;
    }
}