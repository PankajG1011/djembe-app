package com.djembe.app.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank
    private String username;

    @NotBlank
    @Email
    @Indexed(unique = true)
    private String email;

    @NotBlank
    private String passwordHash;

    // e.g. "USER", "ADMIN"
    private Set<String> roles = new HashSet<>(Set.of("USER"));

    @CreatedDate
    private Instant createdAt;

    // Current practice streak, in days
    private int practiceStreak = 0;

    private Instant lastPracticeDate;
}
