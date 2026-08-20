package com.djembe.app.config;

import com.djembe.app.model.Lesson;
import com.djembe.app.model.Rhythm;
import com.djembe.app.repository.LessonRepository;
import com.djembe.app.repository.RhythmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Seeds sample lessons and rhythms on application startup, but only if those
 * collections are currently empty - safe to restart the app repeatedly
 * without creating duplicates.
 *
 * This is a dev/demo convenience, not production seed data - real content
 * would be entered via an admin panel or a one-off migration instead.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final LessonRepository lessonRepository;
    private final RhythmRepository rhythmRepository;

    @Override
    public void run(String... args) {
        if (lessonRepository.count() == 0) {
            seedLessons();
        }
        if (rhythmRepository.count() == 0) {
            seedRhythms();
        }
    }

    private void seedLessons() {
        Lesson l1 = new Lesson();
        l1.setTitle("Sitting Position & Grip");
        l1.setDescription("Proper posture and hand position before playing your first tone");
        l1.setDifficulty("BEGINNER");
        l1.setSequenceOrder(1);
        l1.setContentUrl("https://example.com/lessons/posture");
        l1.setPrerequisiteLessonId(null);
        l1.setTechniquesCovered(List.of("posture"));
        l1 = lessonRepository.save(l1);

        Lesson l2 = new Lesson();
        l2.setTitle("The Bass Tone");
        l2.setDescription("Your first sound: a deep, open hit in the center of the drum");
        l2.setDifficulty("BEGINNER");
        l2.setSequenceOrder(2);
        l2.setContentUrl("https://example.com/lessons/bass-tone");
        l2.setPrerequisiteLessonId(l1.getId());
        l2.setTechniquesCovered(List.of("bass"));
        l2 = lessonRepository.save(l2);

        Lesson l3 = new Lesson();
        l3.setTitle("The Open Tone");
        l3.setDescription("A ringing, mid-pitched sound played near the edge with an open hand");
        l3.setDifficulty("BEGINNER");
        l3.setSequenceOrder(3);
        l3.setContentUrl("https://example.com/lessons/open-tone");
        l3.setPrerequisiteLessonId(l2.getId());
        l3.setTechniquesCovered(List.of("tone"));
        l3 = lessonRepository.save(l3);

        Lesson l4 = new Lesson();
        l4.setTitle("The Slap");
        l4.setDescription("A sharp, cutting sound made with fingers at the very edge of the drum");
        l4.setDifficulty("BEGINNER");
        l4.setSequenceOrder(4);
        l4.setContentUrl("https://example.com/lessons/slap");
        l4.setPrerequisiteLessonId(l3.getId());
        l4.setTechniquesCovered(List.of("slap"));
        l4 = lessonRepository.save(l4);

        Lesson l5 = new Lesson();
        l5.setTitle("Combining Bass, Tone & Slap");
        l5.setDescription("Your first simple pattern, alternating all three tones");
        l5.setDifficulty("INTERMEDIATE");
        l5.setSequenceOrder(1);
        l5.setContentUrl("https://example.com/lessons/first-pattern");
        l5.setPrerequisiteLessonId(l4.getId());
        l5.setTechniquesCovered(List.of("bass", "tone", "slap"));
        lessonRepository.save(l5);
    }

    private void seedRhythms() {
        Rhythm kuku = new Rhythm();
        kuku.setName("Kuku");
        kuku.setRegion("Guinea");
        kuku.setDescription("A celebratory rhythm traditionally played to mark a good catch after fishing");
        kuku.setDefaultBpm(120);
        kuku.setDifficulty("BEGINNER");
        kuku.setAudioTrackUrl("https://example.com/rhythms/kuku.mp3");
        kuku.setPartStemUrls(Map.of("djembe1", "https://example.com/rhythms/kuku-djembe1.mp3"));
        kuku.setTags(List.of("celebration", "traditional"));
        rhythmRepository.save(kuku);

        Rhythm djole = new Rhythm();
        djole.setName("Djole");
        djole.setRegion("Sierra Leone");
        djole.setDescription("An energetic rhythm associated with young men's dances");
        djole.setDefaultBpm(130);
        djole.setDifficulty("INTERMEDIATE");
        djole.setAudioTrackUrl("https://example.com/rhythms/djole.mp3");
        djole.setPartStemUrls(Map.of("djembe1", "https://example.com/rhythms/djole-djembe1.mp3"));
        djole.setTags(List.of("dance", "traditional"));
        rhythmRepository.save(djole);

        Rhythm sunun = new Rhythm();
        sunun.setName("Sunun");
        sunun.setRegion("Mali");
        sunun.setDescription("Used to accompany masked dancers in ceremonial performances");
        sunun.setDefaultBpm(110);
        sunun.setDifficulty("ADVANCED");
        sunun.setAudioTrackUrl("https://example.com/rhythms/sunun.mp3");
        sunun.setPartStemUrls(Map.of("djembe1", "https://example.com/rhythms/sunun-djembe1.mp3"));
        sunun.setTags(List.of("ceremonial", "traditional"));
        rhythmRepository.save(sunun);
    }
}