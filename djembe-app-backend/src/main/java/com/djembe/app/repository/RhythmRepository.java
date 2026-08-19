package com.djembe.app.repository;

import com.djembe.app.model.Rhythm;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RhythmRepository extends MongoRepository<Rhythm, String> {
    List<Rhythm> findByDifficulty(String difficulty);
    List<Rhythm> findByRegionIgnoreCase(String region);
}
