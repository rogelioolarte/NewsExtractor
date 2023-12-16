package com.NewsExtractor.persistence.Impl;

import com.NewsExtractor.entity.Article;
import com.NewsExtractor.persistence.IArticleDAO;
import com.NewsExtractor.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ArticleDAOImpl implements IArticleDAO {
    @Autowired
    private ArticleRepository articleRepository;


    @Override
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    @Override
    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }

    @Override
    public Optional<Article> findByTitle(String title) {
        return articleRepository.findByTitle(title);
    }

    @Override
    public Optional<Article> findByAuthors(String authors) {
        return articleRepository.findByAuthors(authors);
    }

    @Override
    public Optional<Article> findByContent(String content) {
        return articleRepository.findByContent(content);
    }

    @Override
    public Optional<Article> findBySource(String source) {
        return articleRepository.findBySource(source);
    }

    @Override
    public boolean existsBySource(String source) {

        return articleRepository.existsBySource(source);
    }

    @Override
    public void save(Article article) {
        articleRepository.save(article);
    }

    @Override
    public void deleteById(Long id) {
        articleRepository.deleteById(id);
    }

}

