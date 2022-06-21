package com.image.detection.codingexercise.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Builder
public class Object {
    @Id
    private Long id;
    @Column
    private String specificObjectDetected;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Object objectDetected;

    public Object(Object objectDetected) {
        this.specificObjectDetected = objectDetected.specificObjectDetected;
    }
}
