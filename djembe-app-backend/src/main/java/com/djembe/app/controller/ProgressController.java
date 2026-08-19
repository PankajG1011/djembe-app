package com.djembe.app.controller;

import com.djembe.app.model.Progress;
import com.djembe.app.model.User;
import com.djembe.app.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/me")
    public Progress getMyProgress(@AuthenticationPrincipal User user) {
        return progressService.getOrCreateProgress(user.getId());
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public Progress completeLesson(@AuthenticationPrincipal User user, @PathVariable String lessonId) {
        return progressService.completeLesson(user.getId(), lessonId);
    }

    @PostMapping("/rhythms/{rhythmId}/master")
    public Progress masterRhythm(@AuthenticationPrincipal User user, @PathVariable String rhythmId) {
        return progressService.masterRhythm(user.getId(), rhythmId);
    }
}
