package skhu.skhuAdventure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import skhu.skhuAdventure.repository.UsersRepository;
import skhu.skhuAdventure.repository.MajorRepository;
import skhu.skhuAdventure.entity.UsersEntity;
import skhu.skhuAdventure.entity.MajorEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MajorService {
    private static final Logger logger = LoggerFactory.getLogger(MajorService.class);

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MajorRepository majorRepository;

    public String getMajorNameByUserId(String userId) {
        UsersEntity user = usersRepository.findByUserId(userId);
        if (user == null) {
            logger.error("User not found: {}", userId);
            throw new RuntimeException("User not found");
        }

        MajorEntity major = user.getMajor();
        if (major == null) {
            logger.error("Major not found for user: {}", userId);
            throw new RuntimeException("Major not found for user");
        }

        return major.getMajorName();
    }
}