package com.NewsExtractor.persistence.Impl;

import com.NewsExtractor.entity.Pattern;
import com.NewsExtractor.persistence.IPatternDAO;
import com.NewsExtractor.repository.PatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PatternDAOImpl implements IPatternDAO {

    @Autowired
    private PatternRepository patternRepository;

    @Override
    public List<Pattern> findAll() {
        return patternRepository.findAll();
    }

    @Override
    public Optional<Pattern> findById(Long id) {
        return patternRepository.findById(id);
    }

    @Override
    public Optional<Pattern> findByName(String name) {
        return patternRepository.findByName(name);
    }

    @Override
    public boolean existsByName(String name) {
        return patternRepository.existsByName(name);
    }

    @Override
    public void save(Pattern pattern) {
        patternRepository.save(pattern);
    }

    @Override
    public void deleteById(Long id) {
        patternRepository.deleteById(id);
    }

}
