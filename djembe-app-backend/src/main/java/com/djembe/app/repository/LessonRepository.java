package com.djembe.app.repository;

import com.djembe.app.model.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LessonRepository extends MongoRepository<Lesson, String> {
    List<Lesson> findByDifficultyOrderBySequenceOrderAsc(String difficulty);
    List<Lesson> findAllByOrderByDifficultyAscSequenceOrderAsc();
}
