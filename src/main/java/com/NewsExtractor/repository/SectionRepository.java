package com.NewsExtractor.repository;

import com.NewsExtractor.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findByName(String name);

    Optional<Section> findBySource(String source);

    boolean existsBySource(String source);
}
