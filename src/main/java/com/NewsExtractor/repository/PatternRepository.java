package com.NewsExtractor.repository;

import com.NewsExtractor.entity.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, Long> {
    Optional<Pattern> findByName(String name);

    boolean existsByName(String name);
}
