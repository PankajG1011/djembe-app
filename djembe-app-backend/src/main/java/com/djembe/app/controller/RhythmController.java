package com.djembe.app.controller;

import com.djembe.app.model.Rhythm;
import com.djembe.app.repository.RhythmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rhythms")
@RequiredArgsConstructor
public class RhythmController {

    private final RhythmRepository rhythmRepository;

    @GetMapping
    public List<Rhythm> getAll() {
        return rhythmRepository.findAll();
    }

    @GetMapping("/difficulty/{level}")
    public List<Rhythm> getByDifficulty(@PathVariable String level) {
        return rhythmRepository.findByDifficulty(level.toUpperCase());
    }

    @GetMapping("/region/{region}")
    public List<Rhythm> getByRegion(@PathVariable String region) {
        return rhythmRepository.findByRegionIgnoreCase(region);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rhythm> getById(@PathVariable String id) {
        return rhythmRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Rhythm create(@RequestBody Rhythm rhythm) {
        return rhythmRepository.save(rhythm);
    }
}
