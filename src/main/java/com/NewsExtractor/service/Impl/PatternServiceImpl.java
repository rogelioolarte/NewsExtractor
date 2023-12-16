package com.NewsExtractor.service.Impl;

import com.NewsExtractor.entity.Pattern;
import com.NewsExtractor.persistence.IPatternDAO;
import com.NewsExtractor.service.IPatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatternServiceImpl implements IPatternService {

    @Autowired
    private IPatternDAO patternDAO;

    @Override
    public List<Pattern> findAll() {
        return patternDAO.findAll();
    }

    @Override
    public Optional<Pattern> findById(Long id) {
        return patternDAO.findById(id);
    }

    @Override
    public Optional<Pattern> findByName(String name) {
        return patternDAO.findByName(name);
    }

    @Override
    public boolean existsByName(String name) {
        return patternDAO.existsByName(name);
    }

    @Override
    public void save(Pattern pattern) {
        patternDAO.save(pattern);
    }

    @Override
    public void deleteById(Long id) {
        patternDAO.deleteById(id);
    }
}
