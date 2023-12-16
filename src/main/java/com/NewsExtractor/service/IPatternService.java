package com.NewsExtractor.service;

import com.NewsExtractor.entity.Pattern;

import java.util.List;
import java.util.Optional;

public interface IPatternService {
    List<Pattern> findAll();

    Optional<Pattern> findById(Long id);

    Optional<Pattern> findByName(String name);

    boolean existsByName(String name);

    void save(Pattern pattern);

    void deleteById(Long id);
}
