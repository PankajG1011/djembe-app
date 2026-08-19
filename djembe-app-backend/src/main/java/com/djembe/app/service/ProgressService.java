package com.djembe.app.service;

import com.djembe.app.model.Progress;
import com.djembe.app.model.User;
import com.djembe.app.repository.ProgressRepository;
import com.djembe.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final LessonService lessonService;

    public Progress getOrCreateProgress(String userId) {
        return progressRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Progress p = new Progress();
                    p.setUserId(userId);
                    p.setLastUpdated(Instant.now());
                    return progressRepository.save(p);
                });
    }

    public Progress completeLesson(String userId, String lessonId) {
        // FR7: reject completion if the lesson's prerequisite hasn't been
        // completed yet - prevents the endpoint being called out of order.
        lessonService.assertLessonIsUnlocked(userId, lessonId);

        Progress progress = getOrCreateProgress(userId);
        progress.getCompletedLessonIds().add(lessonId);
        progress.setLastUpdated(Instant.now());
        updateStreak(userId);
        return progressRepository.save(progress);
    }

    public Progress masterRhythm(String userId, String rhythmId) {
        Progress progress = getOrCreateProgress(userId);
        progress.getMasteredRhythmIds().add(rhythmId);
        progress.setLastUpdated(Instant.now());
        updateStreak(userId);
        return progressRepository.save(progress);
    }

    // Simple day-based streak: increments if last practice was yesterday,
    // resets to 1 if there was a gap, stays the same if already practiced today.
    private void updateStreak(String userId) {
        userRepository.findById(userId).ifPresent(user -> {
            Instant now = Instant.now();
            Instant last = user.getLastPracticeDate();

            if (last == null) {
                user.setPracticeStreak(1);
            } else {
                long daysBetween = ChronoUnit.DAYS.between(last, now);
                if (daysBetween == 0) {
                    // already practiced today, no change
                } else if (daysBetween == 1) {
                    user.setPracticeStreak(user.getPracticeStreak() + 1);
                } else {
                    user.setPracticeStreak(1);
                }
            }

            user.setLastPracticeDate(now);
            userRepository.save(user);
        });
    }
}
