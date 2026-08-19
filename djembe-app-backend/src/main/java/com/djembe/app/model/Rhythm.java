package com.djembe.app.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rhythms")
public class Rhythm {

    @Id
    private String id;

    @NotBlank
    private String name;

    // e.g. "Guinea", "Mali", "Senegal"
    private String region;

    private String description;

    // Default tempo in BPM; user can adjust playback tempo client-side
    private int defaultBpm;

    // BEGINNER, INTERMEDIATE, ADVANCED
    private String difficulty;

    // URL to a full-ensemble play-along audio track
    private String audioTrackUrl;

    // Individual part stems, e.g. {"djembe1": url, "dundun": url}
    private java.util.Map<String, String> partStemUrls;

    private List<String> tags;
}
