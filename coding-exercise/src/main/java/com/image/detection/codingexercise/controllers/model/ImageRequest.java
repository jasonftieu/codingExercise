package com.image.detection.codingexercise.controllers.model;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ImageRequest {
    @Valid
    @NotNull
    private String url;
    private String imageLabel;
    private Boolean enableObjectDetection;
}
