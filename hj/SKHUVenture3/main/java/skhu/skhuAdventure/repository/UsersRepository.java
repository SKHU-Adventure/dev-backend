package skhu.skhuAdventure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skhu.skhuAdventure.entity.UsersEntity;


@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, String> {
    UsersEntity findByUserId(String userId);  // userId가 String임을 반영
}
