package com.NewsExtractor.service;

import com.NewsExtractor.entity.Article;

import java.util.List;
import java.util.Optional;

public interface IArticleService {
    List<Article> findAll();

    Optional<Article> findById(Long id);

    Optional<Article> findByTitle(String title);

    Optional<Article> findByAuthors(String authors);

    Optional<Article> findByContent(String content);

    Optional<Article> findBySource(String source);

    boolean existsBySource(String source);

    void save(Article article);

    void deleteById(Long id);
}
