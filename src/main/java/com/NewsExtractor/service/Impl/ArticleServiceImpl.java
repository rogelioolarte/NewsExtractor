package com.NewsExtractor.service.Impl;

import com.NewsExtractor.entity.Article;
import com.NewsExtractor.persistence.IArticleDAO;
import com.NewsExtractor.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements IArticleService {

    @Autowired
    private IArticleDAO articleDAO;

    @Override
    @Transactional
    public List<Article> findAll() {
        return articleDAO.findAll();
    }

    @Override
    @Transactional
    public Optional<Article> findById(Long id) {
        return articleDAO.findById(id);
    }

    @Override
    @Transactional
    public Optional<Article> findByTitle(String title) {
        return articleDAO.findByTitle(title);
    }

    @Override
    @Transactional
    public Optional<Article> findByAuthors(String authors) {
        return articleDAO.findByAuthors(authors);
    }

    @Override
    @Transactional
    public Optional<Article> findByContent(String content) {
        return articleDAO.findByContent(content);
    }

    @Override
    @Transactional
    public Optional<Article> findBySource(String source) {
        return articleDAO.findBySource(source);
    }

    @Override
    @Transactional
    public boolean existsBySource(String source) {
        return articleDAO.existsBySource(source);
    }

    @Override
    @Transactional
    public void save(Article article) {
        articleDAO.save(article);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        articleDAO.deleteById(id);
    }
}
