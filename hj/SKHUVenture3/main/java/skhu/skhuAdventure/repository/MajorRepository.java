package skhu.skhuAdventure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skhu.skhuAdventure.entity.MajorEntity;

@Repository
public interface MajorRepository extends JpaRepository<MajorEntity, Integer> {



}