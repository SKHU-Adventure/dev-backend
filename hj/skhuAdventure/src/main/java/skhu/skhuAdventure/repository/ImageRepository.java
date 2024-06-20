package skhu.skhuAdventure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skhu.skhuAdventure.entity.ImageEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    int countByUserId(String userId); // userId를 String으로 처리하도록 수정
}
