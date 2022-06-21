package com.image.detection.codingexercise.controllers;

import com.image.detection.codingexercise.exceptions.NoSuchElementFoundException;
import com.image.detection.codingexercise.exceptions.ResourceNotFoundException;
import com.image.detection.codingexercise.controllers.model.ImageRequest;
import com.image.detection.codingexercise.controllers.model.ImageResponse;
import com.image.detection.codingexercise.controllers.model.SpecificImageResponse;
import com.image.detection.codingexercise.entities.Image;
import com.image.detection.codingexercise.services.ImageObjectDetectionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RestController
public class VisionController {
    @Autowired
    private ImageObjectDetectionService imageObjectDetectionService;

    @GetMapping("/images")
    public ResponseEntity<List<Image>> getAllImages() {
        List<Image> response = imageObjectDetectionService.getAllImages();
        if (response.isEmpty()) {
            throw new NoSuchElementFoundException("No Images were found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/imagesByObject")
    public ResponseEntity<SpecificImageResponse> getImagesByObjects(@RequestParam List<String> objects) {
        if (CollectionUtils.isEmpty(objects)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Objects must not be null or empty");
        }
        List<Image> matchedImages = imageObjectDetectionService.getImagesByObject(objects);
        List<String> listOfImages = new ArrayList<>();
        matchedImages.stream().forEach(image -> {
            listOfImages.add(image.getUrl());
        });
        SpecificImageResponse response = SpecificImageResponse
                .builder()
                .urls(listOfImages)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/images/{id}")
    public Image getImage(@PathVariable Long id) throws ResourceNotFoundException {
        if (Objects.isNull(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must not be empty or null");
        }
        return imageObjectDetectionService.getImage(id);
    }

    @PostMapping("/images")
    public ResponseEntity<ImageResponse> createImage(@RequestBody ImageRequest imageRequest) {
        if (StringUtils.isBlank(imageRequest.getUrl())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL must be inputted or cannot be blank");
        }
        ImageResponse response = imageObjectDetectionService.saveOrUpdateImage(imageRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}