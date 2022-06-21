package com.image.detection.codingexercise.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Builder
public class Image {
    @Id
    private Long image_id;
    @Column
    private String url;
    @Column(name = "image_metadata")
    @Lob
    private String imageMetadata;
    @Column
    private String objectLabel;
    @JoinColumn(name = "object_foreign_key")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Object> objectsDetected;
}
