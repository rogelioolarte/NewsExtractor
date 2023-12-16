package com.NewsExtractor.persistence;

import com.NewsExtractor.entity.NewsPaper;

import java.util.List;
import java.util.Optional;

public interface INewsPaperDAO {
    List<NewsPaper> findAll();

    Optional<NewsPaper> findById(Long id);

    Optional<NewsPaper> findByName(String name);

    Optional<NewsPaper> findBySource(String source);

    boolean existsBySource(String source);

    void save(NewsPaper newsPaper);

    void deleteById(Long id);
}
