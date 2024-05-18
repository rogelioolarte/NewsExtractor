package com.NewsExtractor.service.Impl;

import com.NewsExtractor.entity.Pattern;
import com.NewsExtractor.persistence.IPatternDAO;
import com.NewsExtractor.service.IPatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatternServiceImpl implements IPatternService {

    @Autowired
    private IPatternDAO patternDAO;

    @Override
    @Transactional
    public List<Pattern> findAll() {
        return patternDAO.findAll();
    }

    @Override
    @Transactional
    public Optional<Pattern> findById(Long id) {
        return patternDAO.findById(id);
    }

    @Override
    @Transactional
    public Optional<Pattern> findByName(String name) {
        return patternDAO.findByName(name);
    }

    @Override
    @Transactional
    public boolean existsByName(String name) {
        return patternDAO.existsByName(name);
    }

    @Override
    @Transactional
    public void save(Pattern pattern) {
        patternDAO.save(pattern);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        patternDAO.deleteById(id);
    }
}
