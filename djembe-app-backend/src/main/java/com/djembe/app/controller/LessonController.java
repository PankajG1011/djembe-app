package com.djembe.app.controller;

import com.djembe.app.dto.LessonWithUnlockStatus;
import com.djembe.app.model.Lesson;
import com.djembe.app.model.User;
import com.djembe.app.repository.LessonRepository;
import com.djembe.app.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonRepository lessonRepository;
    private final LessonService lessonService;

    @GetMapping
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAllByOrderByDifficultyAscSequenceOrderAsc();
    }

    // FR7: same lesson list, but each entry flags whether the current user
    // (or guest, if unauthenticated) has unlocked it yet. Frontend should use
    // this endpoint instead of the plain one above once a user is logged in.
    @GetMapping("/with-status")
    public List<LessonWithUnlockStatus> getLessonsWithUnlockStatus(@AuthenticationPrincipal User user) {
        String userId = user != null ? user.getId() : null;
        return lessonService.getLessonsWithUnlockStatus(userId);
    }

    @GetMapping("/difficulty/{level}")
    public List<Lesson> getByDifficulty(@PathVariable String level) {
        return lessonRepository.findByDifficultyOrderBySequenceOrderAsc(level.toUpperCase());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getById(@PathVariable String id) {
        return lessonRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Admin-only in a real deployment - left open here for portfolio/demo simplicity
    @PostMapping
    public Lesson create(@RequestBody Lesson lesson) {
        return lessonRepository.save(lesson);
    }
}
