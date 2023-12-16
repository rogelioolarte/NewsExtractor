package com.NewsExtractor.controller;

import com.NewsExtractor.dto.ArticleDTO;
import com.NewsExtractor.entity.Article;
import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.entity.Section;
import com.NewsExtractor.exception.SpecificException;
import com.NewsExtractor.service.IArticleService;
import com.NewsExtractor.service.INewsPaperService;
import com.NewsExtractor.service.ISectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/article")
@Tag(name = "Article Controller", description = "Manage all items accordingly.")
public class ArticleController {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private INewsPaperService newsPaperService;

    @Autowired
    private ISectionService sectionService;


    @Operation(summary = "Find an article using a id parameter.",
            description = "Find an article using a id parameter that exists.")
    @GetMapping("/find/{id}")
    public ResponseEntity<ArticleDTO> findById(@PathVariable Long id) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<Article> articleOptional = articleService.findById(id);
        if (articleOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The article id does not exist");

        Article article = articleOptional.get();
        ArticleDTO articleDTO = ArticleDTO.builder().id(article.getId())
                .title(article.getTitle()).source(article.getSource())
                .authors(article.getAuthors()).content(article.getContent())
                .section(article.getSection()).newsPaper(article.getNewsPaper()).build();
        return ResponseEntity.ok(articleDTO);
    }

    @Operation(summary = "Find all the articles.",
            description = "Find all the articles that exists.")
    @GetMapping("/findAll")
    public ResponseEntity<List<ArticleDTO>> findAll() {
        List<ArticleDTO> articleDTOList = articleService.findAll().stream()
                .map(article -> ArticleDTO.builder().id(article.getId())
                        .title(article.getTitle()).source(article.getSource())
                        .authors(article.getAuthors())
                        .content(article.getContent()).section(article.getSection())
                        .newsPaper(article.getNewsPaper()).build()).toList();
        return ResponseEntity.ok(articleDTOList);
    }

    @Operation(summary = "Save an article using a request body.",
            description = "Save an article using the request body, remember to check " +
                    "the schemas to understand and to save any article you need newspaper " +
                    "object and a section object with their unique identifiers.")
    @PostMapping("/save")
    public ResponseEntity<URI> save(@RequestBody ArticleDTO articleDTO) throws URISyntaxException {
        if (articleDTO.getNewsPaper().getId() == null ||
                articleDTO.getNewsPaper().getId().toString().isBlank()) throw
                new SpecificException(HttpStatus.BAD_REQUEST, "At least the newspaper id is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(articleDTO
                .getNewsPaper().getId());
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The newspaper to be related to the article does not exist");
        if (articleDTO.getSection().getId() == null ||
                articleDTO.getSection().getId().toString().isBlank()) throw
                new SpecificException(HttpStatus.BAD_REQUEST, "At least the section id is required");
        Optional<Section> sectionOptional = sectionService.findById(articleDTO
                .getSection().getId());
        if (sectionOptional.isEmpty()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The section to be related to the article does not exist");
        if (articleDTO.getSource() == null || articleDTO.getSource().isBlank())
            throw new SpecificException(HttpStatus.BAD_REQUEST,
                    "The source of the article is required");
        if (articleService.existsBySource(articleDTO.getSource())) throw
                new SpecificException(HttpStatus.CONFLICT, "The article already exists");

        articleService.save(Article.builder().source(articleDTO.getSource())
                .title(articleDTO.getTitle() == null || articleDTO.getTitle().isBlank() ?
                        "" : articleDTO.getTitle())
                .authors(articleDTO.getAuthors() == null || articleDTO.getAuthors().isBlank() ?
                        "" : articleDTO.getAuthors())
                .content(articleDTO.getContent() == null || articleDTO.getContent().isBlank() ?
                        "" : articleDTO.getContent())
                .section(articleDTO.getSection()).newsPaper(articleDTO.getNewsPaper()).build());

        Optional<Article> articleOptional = articleService.findBySource(articleDTO.getSource());
        if (articleOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The article was not added");
        return ResponseEntity.created(new URI(String.format("/api/article/find/%d",
                articleOptional.get().getId()))).build();
    }

    @Operation(summary = "Update an article using a id parameter and a request body.",
            description = "Update an article using a id parameter and a request body, " +
                    "remember to check the schemas to understand and to save any article " +
                    "you need newspaper object and a section object with their unique " +
                    "identifiers.")
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateById(@PathVariable Long id,
                                             @RequestBody ArticleDTO articleDTO) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<Article> articleOptional = articleService.findById(id);
        if (articleOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The article id does not exist");
        if (articleDTO.getNewsPaper().getId() == null ||
                articleDTO.getNewsPaper().getId().toString().isBlank()) throw
                new SpecificException(HttpStatus.BAD_REQUEST,
                        "At least the newspaper id is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(articleDTO
                .getNewsPaper().getId());
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The newspaper to be related to the article does not exist");
        if (articleDTO.getSection().getId() == null ||
                articleDTO.getSection().getId().toString().isBlank()) throw
                new SpecificException(HttpStatus.BAD_REQUEST, "At least the section id is required");
        Optional<Section> sectionOptional = sectionService.findById(articleDTO
                .getSection().getId());
        if (sectionOptional.isEmpty()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The section to be related to the article does not exist");
        if (articleDTO.getSource() == null || articleDTO.getSource().isBlank())
            throw new SpecificException(HttpStatus.BAD_REQUEST, "The source of the article is required");

        Article article = settArticle(articleDTO, articleOptional.get());
        articleService.save(article);
        return ResponseEntity.ok(String.format("Item with updated Id %d", id));
    }

    private Article settArticle(ArticleDTO articleDTO, Article article) {
        article.setSource(articleDTO.getSource());
        article.setTitle(articleDTO.getTitle() == null || articleDTO.getTitle().isBlank() ?
                "" : articleDTO.getTitle());
        article.setAuthors(articleDTO.getAuthors() == null || articleDTO.getAuthors().isBlank() ?
                "" : articleDTO.getAuthors());
        article.setContent(articleDTO.getContent() == null || articleDTO.getContent().isBlank() ?
                "" : articleDTO.getContent());
        article.setSection(articleDTO.getSection());
        article.setNewsPaper(articleDTO.getNewsPaper());
        return article;
    }

    @Operation(summary = "Delete an article using a id parameter.",
            description = "Delete an article using a id parameter that exists.")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        if (id == null) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<Article> articleOptional = articleService.findById(id);
        if (articleOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The section id does not exist");

        articleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
