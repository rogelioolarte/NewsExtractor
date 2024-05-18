package com.NewsExtractor.repository;

import com.NewsExtractor.entity.NewsPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsPaperRepository extends JpaRepository<NewsPaper, Long> {
    Optional<NewsPaper> findByName(String name);

    Optional<NewsPaper> findBySource(String source);

    boolean existsBySource(String source);
}
