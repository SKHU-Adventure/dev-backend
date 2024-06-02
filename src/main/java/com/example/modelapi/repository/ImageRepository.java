package com.example.modelapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.modelapi.entity.ImageEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}
