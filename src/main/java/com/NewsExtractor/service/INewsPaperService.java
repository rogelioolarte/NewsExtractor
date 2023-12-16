package com.NewsExtractor.service;

import com.NewsExtractor.entity.NewsPaper;

import java.util.List;
import java.util.Optional;

public interface INewsPaperService {
    List<NewsPaper> findAll();

    Optional<NewsPaper> findById(Long id);

    Optional<NewsPaper> findByName(String name);

    Optional<NewsPaper> findBySource(String source);

    boolean existsBySource(String source);

    void cleanSections(NewsPaper newsPaper);

    void cleanArticles(NewsPaper newsPaper);

    void cleanArticlesWithOutSource(NewsPaper newsPaper);
    void cleanPatterns(NewsPaper newsPaper);
    void cleanProxyList(NewsPaper newsPaper);

    void save(NewsPaper newsPaper);

    void deleteById(Long id);
}
