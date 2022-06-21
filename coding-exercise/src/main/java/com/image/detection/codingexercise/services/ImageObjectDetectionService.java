package com.image.detection.codingexercise.services;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import com.image.detection.codingexercise.exceptions.ResourceNotFoundException;
import com.image.detection.codingexercise.controllers.model.ImageRequest;
import com.image.detection.codingexercise.controllers.model.ImageResponse;
import com.image.detection.codingexercise.entities.Image;
import com.image.detection.codingexercise.repositories.ImageRepository;
import com.image.detection.codingexercise.entities.Object;
import com.image.detection.codingexercise.repositories.ObjectsDetectedRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ImageObjectDetectionService {
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    ObjectsDetectedRepository objectsDetectedRepository;
    @Autowired
    private CloudVisionTemplate cloudVisionTemplate;
    @Autowired
    private ResourceLoader resourceLoader;

    public Image getImage(Long id) throws ResourceNotFoundException {
        return imageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + id + " not found"));
    }

    public ImageResponse saveOrUpdateImage(ImageRequest imageRequest) {
        ImageResponse imageResponse = null;
        try {
            AnnotateImageResponse response = detectObjectsFromImage(imageRequest);
            List<Object> objectsDetected = new ArrayList<>();
            if (imageRequest.getEnableObjectDetection() != null && imageRequest.getEnableObjectDetection()) {
                response.getLabelAnnotationsList().stream()
                        .forEach(entity -> {
                            Object object = Object.builder()
                                    .specificObjectDetected(entity.getDescription())
                                    .id(RandomUtils.nextLong())
                                    .build();
                            objectsDetected.add(object);
                        });
            }
            Image image = Image
                    .builder()
                    .url(imageRequest.getUrl())
                    .image_id(RandomUtils.nextLong())
                    .imageMetadata(response.getLabelAnnotationsList().toString())
                    .objectLabel(StringUtils.isBlank(imageRequest.getImageLabel()) ? response.getLabelAnnotationsList().get(0).getDescription() : imageRequest.getImageLabel())
                    .objectsDetected(objectsDetected)
                    .build();

            imageResponse = ImageResponse
                    .builder()
                    .imageData(image.getImageMetadata())
                    .id(image.getImage_id())
                    .label(StringUtils.isBlank(imageRequest.getImageLabel()) ? response.getLabelAnnotationsList().get(0).getDescription() : imageRequest.getImageLabel())
                    .objectsDetected(imageRequest.getEnableObjectDetection() != null ? objectsDetected : null)
                    .build();
            imageRepository.save(image);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return imageResponse;
    }

    public List<Image> getAllImages() {
        List<Image> images = null;
        try {
            images = imageRepository.findAll();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return images;
    }

    public List<Image> getImagesByObject(List<String> keywords) {
        List<Image> listOfImages = getAllImages();
        List<Image> matchedImages = new ArrayList<>();
        try {
            listOfImages.stream().forEach(image -> {
                image.getObjectsDetected().stream().forEach(object -> {
                    keywords.stream().forEach(keyword -> {
                        if (keyword.equalsIgnoreCase(object.getSpecificObjectDetected()) || keyword.equalsIgnoreCase(image.getObjectLabel())) {
                            Image matchedImage = null;
                            try {
                                matchedImage = getImage(image.getImage_id());
                                if (matchedImages.contains(matchedImage)) {
                                    return;
                                }
                            } catch (ResourceNotFoundException e) {
                                e.printStackTrace();
                            }
                            matchedImages.add(matchedImage);
                        }
                    });
                });
            });
        } catch (Exception e) {
            log.error(e.toString());
        }
        return matchedImages;
    }

    private AnnotateImageResponse detectObjectsFromImage(ImageRequest imageRequest) {
        Resource imageResource = this.resourceLoader.getResource(imageRequest.getUrl());
        return this.cloudVisionTemplate.analyzeImage(
                imageResource, Feature.Type.LABEL_DETECTION);
    }
}
