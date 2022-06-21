package com.image.detection.codingexercise.repositories;

import com.image.detection.codingexercise.entities.Object;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObjectsDetectedRepository extends JpaRepository<Object, Long> {
}
