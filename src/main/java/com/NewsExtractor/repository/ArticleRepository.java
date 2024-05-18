package com.NewsExtractor.repository;

import com.NewsExtractor.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByTitle(String title);

    Optional<Article> findByAuthors(String authors);

    Optional<Article> findByContent(String content);

    Optional<Article> findBySource(String source);

    boolean existsBySource(String source);
}
