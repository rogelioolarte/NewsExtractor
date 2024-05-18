package com.NewsExtractor.service.Impl;

import com.NewsExtractor.entity.Article;
import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.persistence.INewsPaperDAO;
import com.NewsExtractor.service.INewsPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NewsPaperServiceImpl implements INewsPaperService {

    @Autowired
    private INewsPaperDAO newsPaperDAO;


    @Override
    @Transactional
    public List<NewsPaper> findAll() {
        return newsPaperDAO.findAll();
    }

    @Override
    @Transactional
    public Optional<NewsPaper> findById(Long id) {
        return newsPaperDAO.findById(id);
    }

    @Override
    @Transactional
    public Optional<NewsPaper> findByName(String name) {
        return newsPaperDAO.findByName(name);
    }

    @Override
    @Transactional
    public Optional<NewsPaper> findBySource(String source) {
        return newsPaperDAO.findBySource(source);
    }

    @Override
    @Transactional
    public boolean existsBySource(String source) {
        return newsPaperDAO.existsBySource(source);
    }

    @Override
    @Transactional
    public void cleanSections(NewsPaper newsPaper) {
        newsPaper.getSectionList().clear();
    }

    @Override
    @Transactional
    public void cleanArticles(NewsPaper newsPaper) {
        newsPaper.getArticleList().clear();
    }

    @Override
    @Transactional
    public void cleanArticlesWithOutSource(NewsPaper newsPaper) {
        for (Article article : newsPaper.getArticleList()) {
            article.setTitle("");
            article.setAuthors("");
            article.setContent("");
        }
    }

    @Override
    @Transactional
    public void cleanPatterns(NewsPaper newsPaper) {
        newsPaper.getPatternList().clear();
    }

    @Override
    @Transactional
    public void cleanProxyList(NewsPaper newsPaper) {
        newsPaper.getProxyyList().clear();
    }

    @Override
    @Transactional
    public void save(NewsPaper newsPaper) {
        newsPaperDAO.save(newsPaper);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        newsPaperDAO.deleteById(id);
    }
}
