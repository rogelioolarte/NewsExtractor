package com.NewsExtractor.persistence;

import com.NewsExtractor.entity.Section;

import java.util.List;
import java.util.Optional;

public interface ISectionDAO {

    List<Section> findAll();

    Optional<Section> findById(Long id);

    Optional<Section> findByName(String name);

    Optional<Section> findBySource(String source);

    boolean existsBySource(String source);

    void save(Section section);

    void deleteById(Long id);
}
