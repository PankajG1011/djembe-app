package com.djembe.app.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "lessons")
public class Lesson {

    @Id
    private String id;

    @NotBlank
    private String title;

    private String description;

    // BEGINNER, INTERMEDIATE, ADVANCED
    private String difficulty;

    // Order within its difficulty tier, used for progressive unlock
    private int sequenceOrder;

    // URL to video or audio-guided lesson content
    private String contentUrl;

    // Lesson must be completed before this one unlocks; null = first lesson
    private String prerequisiteLessonId;

    // Djembe techniques covered, e.g. ["bass", "tone", "slap"]
    private java.util.List<String> techniquesCovered;
}
