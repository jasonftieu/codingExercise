package com.image.detection.codingexercise.repositories;

import com.image.detection.codingexercise.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
