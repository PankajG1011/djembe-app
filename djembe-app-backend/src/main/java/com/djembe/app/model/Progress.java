package com.djembe.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "progress")
public class Progress {

    @Id
    private String id;

    @Indexed
    private String userId;

    private Set<String> completedLessonIds = new HashSet<>();

    private Set<String> masteredRhythmIds = new HashSet<>();

    private Instant lastUpdated;

    // Total practice minutes logged (optional, for future analytics)
    private long totalPracticeMinutes = 0;
}
