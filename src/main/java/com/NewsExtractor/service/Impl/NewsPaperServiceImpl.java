package com.NewsExtractor.service.Impl;

import com.NewsExtractor.entity.Article;
import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.entity.Pattern;
import com.NewsExtractor.persistence.INewsPaperDAO;
import com.NewsExtractor.service.INewsPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsPaperServiceImpl implements INewsPaperService {

    @Autowired
    private INewsPaperDAO newsPaperDAO;


    @Override
    public List<NewsPaper> findAll() {
        return newsPaperDAO.findAll();
    }

    @Override
    public Optional<NewsPaper> findById(Long id) {
        return newsPaperDAO.findById(id);
    }

    @Override
    public Optional<NewsPaper> findByName(String name) {
        return newsPaperDAO.findByName(name);
    }

    @Override
    public Optional<NewsPaper> findBySource(String source) {
        return newsPaperDAO.findBySource(source);
    }

    @Override
    public boolean existsBySource(String source) {
        return newsPaperDAO.existsBySource(source);
    }

    @Override
    public void cleanSections(NewsPaper newsPaper) {
        newsPaper.getSectionList().clear();
    }

    @Override
    public void cleanArticles(NewsPaper newsPaper) {
        newsPaper.getArticleList().clear();
    }

    @Override
    public void cleanArticlesWithOutSource(NewsPaper newsPaper) {
        for (Article article : newsPaper.getArticleList()) {
            article.setTitle("");
            article.setAuthors("");
            article.setContent("");
        }
    }

    @Override
    public void cleanPatterns(NewsPaper newsPaper) {
        newsPaper.getPatternList().clear();
    }

    @Override
    public void cleanProxyList(NewsPaper newsPaper) {
        newsPaper.getProxyyList().clear();
    }

    @Override
    public void save(NewsPaper newsPaper) {
        newsPaperDAO.save(newsPaper);
    }

    @Override
    public void deleteById(Long id) {
        newsPaperDAO.deleteById(id);
    }
}
