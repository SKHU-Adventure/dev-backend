package skhu.skhuAdventure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skhu.skhuAdventure.entity.ImageEntity;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    int countByUserId(String userId);
    List<ImageEntity> findByUserId(String userId);
}