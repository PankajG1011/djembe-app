package com.djembe.app.repository;

import com.djembe.app.model.Progress;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProgressRepository extends MongoRepository<Progress, String> {
    Optional<Progress> findByUserId(String userId);
}
