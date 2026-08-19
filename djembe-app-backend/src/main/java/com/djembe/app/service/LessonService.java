package com.djembe.app.service;

import com.djembe.app.dto.LessonWithUnlockStatus;
import com.djembe.app.model.Lesson;
import com.djembe.app.model.Progress;
import com.djembe.app.repository.LessonRepository;
import com.djembe.app.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ProgressRepository progressRepository;

    /**
     * Returns all lessons annotated with whether the given user has unlocked
     * each one, per FR7: a lesson unlocks once its prerequisiteLessonId has
     * been completed (or immediately, if it has no prerequisite).
     *
     * userId may be null for guest browsing - in that case only lessons with
     * no prerequisite are marked unlocked, so guests can preview the entry
     * point of each track without seeing locked content as playable.
     */
    public List<LessonWithUnlockStatus> getLessonsWithUnlockStatus(String userId) {
        List<Lesson> lessons = lessonRepository.findAllByOrderByDifficultyAscSequenceOrderAsc();
        Set<String> completedLessonIds = getCompletedLessonIds(userId);

        return lessons.stream()
                .map(lesson -> new LessonWithUnlockStatus(lesson, isUnlocked(lesson, completedLessonIds)))
                .toList();
    }

    /**
     * Throws if the user tries to complete a lesson that isn't unlocked yet,
     * so completion can't be spoofed by calling the endpoint out of order.
     */
    public void assertLessonIsUnlocked(String userId, String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));

        Set<String> completedLessonIds = getCompletedLessonIds(userId);

        if (!isUnlocked(lesson, completedLessonIds)) {
            throw new IllegalStateException(
                    "Lesson '" + lesson.getTitle() + "' is locked - complete its prerequisite first");
        }
    }

    private boolean isUnlocked(Lesson lesson, Set<String> completedLessonIds) {
        String prerequisiteId = lesson.getPrerequisiteLessonId();
        if (prerequisiteId == null || prerequisiteId.isBlank()) {
            return true; // first lesson in a track - always unlocked
        }
        return completedLessonIds.contains(prerequisiteId);
    }

    private Set<String> getCompletedLessonIds(String userId) {
        if (userId == null) {
            return Set.of();
        }
        Optional<Progress> progress = progressRepository.findByUserId(userId);
        return progress.map(Progress::getCompletedLessonIds).orElse(Set.of());
    }
}
