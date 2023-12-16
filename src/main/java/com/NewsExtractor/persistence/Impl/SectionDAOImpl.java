package com.NewsExtractor.persistence.Impl;

import com.NewsExtractor.entity.Section;
import com.NewsExtractor.persistence.ISectionDAO;
import com.NewsExtractor.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SectionDAOImpl implements ISectionDAO {

    @Autowired
    private SectionRepository sectionRepository;

    @Override
    public List<Section> findAll() {
        return sectionRepository.findAll();
    }

    @Override
    public Optional<Section> findById(Long id) {
        return sectionRepository.findById(id);
    }

    @Override
    public Optional<Section> findByName(String name) {
        return sectionRepository.findByName(name);
    }

    @Override
    public Optional<Section> findBySource(String source) {
        return sectionRepository.findBySource(source);
    }

    @Override
    public boolean existsBySource(String source) {
        return sectionRepository.existsBySource(source);
    }

    @Override
    public void save(Section section) {
        sectionRepository.save(section);
    }

    @Override
    public void deleteById(Long id) {
        sectionRepository.deleteById(id);
    }

}
