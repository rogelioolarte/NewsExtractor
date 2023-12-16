package com.NewsExtractor.persistence.Impl;

import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.persistence.INewsPaperDAO;
import com.NewsExtractor.repository.NewsPaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NewsPaperDAOImpl implements INewsPaperDAO {

    @Autowired
    private NewsPaperRepository newsPaperRepository;

    @Override
    public List<NewsPaper> findAll() {
        return newsPaperRepository.findAll();
    }

    @Override
    public Optional<NewsPaper> findById(Long id) {
        return newsPaperRepository.findById(id);
    }

    @Override
    public Optional<NewsPaper> findByName(String name) {
        return newsPaperRepository.findByName(name);
    }

    @Override
    public Optional<NewsPaper> findBySource(String source) {
        return newsPaperRepository.findBySource(source);
    }

    @Override
    public boolean existsBySource(String source) {
        return newsPaperRepository.existsBySource(source);
    }

    @Override
    public void save(NewsPaper newsPaper) {
        newsPaperRepository.save(newsPaper);
    }

    @Override
    public void deleteById(Long id) {
        newsPaperRepository.deleteById(id);
    }

}
