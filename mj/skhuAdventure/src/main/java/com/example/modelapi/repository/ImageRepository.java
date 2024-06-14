package com.example.modelapi.repository;

import com.example.modelapi.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    int countByUserEmail(String userEmail);
}
