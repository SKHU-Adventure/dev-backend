package skhu.skhuAdventure.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skhu.skhuAdventure.entity.ImageEntity;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    int countByUserId(String userId);  // 메서드명을 필드명에 맞춰 수정

    List<ImageEntity> findByUserId(String userId);
}
