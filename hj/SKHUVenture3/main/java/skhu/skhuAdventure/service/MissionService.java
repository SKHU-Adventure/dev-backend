package skhu.skhuAdventure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skhu.skhuAdventure.repository.MissionRepository;
import skhu.skhuAdventure.repository.UsersRepository;
import skhu.skhuAdventure.repository.BuildingRepository;
import skhu.skhuAdventure.entity.MissionEntity;
import skhu.skhuAdventure.entity.UsersEntity;
import skhu.skhuAdventure.entity.BuildingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class MissionService {
    private static final Logger logger = LoggerFactory.getLogger(MissionService.class);

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BuildingRepository buildingRepository;


    // 미션 초기화 api
    @Transactional
    public void initializeMissionsForUser(String userId) {
        UsersEntity user = usersRepository.findByUserId(userId);
        if (user == null) {
            logger.error("User not found: {}", userId);
            throw new RuntimeException("User not found");
        }

        List<BuildingEntity> allBuildings = buildingRepository.findAll();
        logger.info("Found {} buildings", allBuildings.size());

        if (allBuildings.isEmpty()) {
            logger.error("No buildings found in the database");
            throw new RuntimeException(" 빌딩이 빈 이슈 : No buildings available for mission initialization");
        }

        for (BuildingEntity building : allBuildings) {
            try {
                MissionEntity existingMission = missionRepository.findByUserAndBuilding(user, building);
                if (existingMission == null) {
                    MissionEntity mission = MissionEntity.builder()
                            .user(user)
                            .building(building)
                            .complete(0)
                            .build();
                    missionRepository.save(mission);
                    logger.info("Created mission for user {} and building {}", userId, building.getId());
                } else {
                    logger.info("Mission already exists for user {} and building {}", userId, building.getId());
                }
            } catch (Exception e) {
                logger.error("Error creating mission for user {} and building {}: {}", userId, building.getId(), e.getMessage());
                throw new RuntimeException("Error creating mission: " + e.getMessage());
            }
        }
    }


    // 미션을 가져오는 api
    public List<MissionEntity> getUserMissions(String userId) {
        UsersEntity user = usersRepository.findByUserId(userId);
        if (user == null) {
            logger.error("User not found: {}", userId);
            throw new RuntimeException("User not found");
        }
        return missionRepository.findByUser(user);
    }
    @Transactional
    public void updateMissionStatus(String userId, Integer buildingId, Integer completeStatus) {
        logger.info("Updating mission status for user: {} and building: {}", userId, buildingId);
        UsersEntity user = usersRepository.findByUserId(userId);
        if (user == null) {
            logger.error("User not found: {}", userId);
            throw new RuntimeException("User not found");
        }

        BuildingEntity building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> {
                    logger.error("Building not found: {}", buildingId);
                    return new RuntimeException("Building not found");
                });

        MissionEntity mission = missionRepository.findByUserAndBuilding(user, building);
        if (mission == null) {
            logger.error("Mission not found for user: {} and building: {}", userId, buildingId);
            throw new RuntimeException("Mission not found");
        }

        mission.setComplete(completeStatus);
        missionRepository.save(mission);
        logger.info("Mission status updated for user: {} and building: {}", userId, buildingId);
    }

    // 새로 추가한 미션 완료 api
    @Transactional
    public void completeMission(String userId, Integer buildingId) {
        UsersEntity user = usersRepository.findByUserId(userId);
        if (user == null) {
            logger.error("User not found: {}", userId);
            throw new RuntimeException("User not found");
        }

        BuildingEntity building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> {
                    logger.error("Building not found: {}", buildingId);
                    return new RuntimeException("Building not found");
                });

        MissionEntity mission = missionRepository.findByUserAndBuilding(user, building);
        if (mission == null) {
            logger.error("Mission not found for user: {} and building: {}", userId, buildingId);
            throw new RuntimeException("Mission not found");
        }

        mission.setComplete(1);
        missionRepository.save(mission);
        logger.info("Mission completed for user: {} and building: {}", userId, buildingId);
    }

}