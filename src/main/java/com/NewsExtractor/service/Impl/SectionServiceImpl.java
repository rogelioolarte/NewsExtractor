package com.NewsExtractor.service.Impl;

import com.NewsExtractor.entity.Section;
import com.NewsExtractor.persistence.ISectionDAO;
import com.NewsExtractor.service.ISectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class SectionServiceImpl implements ISectionService {

    @Autowired
    private ISectionDAO sectionDAO;

    @Override
    public List<Section> findAll() {
        return sectionDAO.findAll();
    }

    @Override
    public Optional<Section> findById(Long id) {
        return sectionDAO.findById(id);
    }

    @Override
    public Optional<Section> findByName(String name) {
        return sectionDAO.findByName(name);
    }

    @Override
    public Optional<Section> findBySource(String source) {
        return sectionDAO.findBySource(source);
    }

    @Override
    public boolean existsBySource(String source) {
        return sectionDAO.existsBySource(source);
    }

    @Override
    public void save(Section section) {
        section.setName(sourceToName(section.getSource()));
        sectionDAO.save(section);
    }

    @Override
    public void deleteById(Long id) {
        sectionDAO.deleteById(id);
    }

    public String sourceToName(String source) {
        String[] segments = URI.create(source).getPath().split("/");
        return segments[1].contains("/") ? URI.create(source).getPath() : segments[1];
    }
}
