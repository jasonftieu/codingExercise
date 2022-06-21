package com.image.detection.codingexercise.controllers.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.image.detection.codingexercise.entities.Object;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
    private String imageData;
    private String label;
    private Long id;
    @JsonIgnore
    private List<Object> objectsDetected;
}
