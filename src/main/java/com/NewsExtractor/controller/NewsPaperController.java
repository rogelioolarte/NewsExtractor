package com.NewsExtractor.controller;

import com.NewsExtractor.dto.NewsPaperDTO;
import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.exception.SpecificException;
import com.NewsExtractor.service.IManagementService;
import com.NewsExtractor.service.INewsPaperService;
import com.NewsExtractor.service.IProxyyService;
import com.NewsExtractor.service.ITestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/newspaper")
@Tag(name = "NewsPaper Controller", description = "Manage all items accordingly.")
public class NewsPaperController {

    @Autowired
    private INewsPaperService newsPaperService;

    @Autowired
    private IManagementService managementService;

    @Autowired
    private IProxyyService proxyyService;

    @Autowired
    private ITestService testService;

    @Operation(summary = "Find a newspaper using a id parameter.",
            description = "Find a newspaper using a id parameter that exists.")
    @GetMapping("/find/{id}")
    public ResponseEntity<NewsPaperDTO> findById(@PathVariable Long id) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The newspaper id does not exist");

        NewsPaper newsPaper = newsPaperOptional.get();
        NewsPaperDTO newsPaperDTO = NewsPaperDTO.builder()
                .id(newsPaper.getId()).name(newsPaper.getName())
                .source(newsPaper.getSource()).total_sections(newsPaper.getTotal_sections())
                .total_articles(newsPaper.getTotal_articles()).progress(newsPaper.getProgress())
                .patternList(newsPaper.getPatternList()).sectionList(newsPaper.getSectionList())
                .articleList(newsPaper.getArticleList())
                .proxyyList(newsPaper.getProxyyList()).build();
        return ResponseEntity.ok(newsPaperDTO);
    }

    @Operation(summary = "Find all the newspapers.",
            description = "Find all the newspapers that exists.")
    @GetMapping("/findAll")
    public ResponseEntity<List<NewsPaperDTO>> findAll() {
        List<NewsPaperDTO> newsPaperDTOList = newsPaperService.findAll().stream()
                .map(newsPaper -> NewsPaperDTO.builder()
                        .id(newsPaper.getId()).name(newsPaper.getName())
                        .source(newsPaper.getSource())
                        .total_sections(newsPaper.getTotal_sections())
                        .total_articles(newsPaper.getTotal_articles())
                        .progress(newsPaper.getProgress())
                        .patternList(newsPaper.getPatternList())
                        .sectionList(newsPaper.getSectionList())
                        .articleList(newsPaper.getArticleList())
                        .proxyyList(newsPaper.getProxyyList()).build()).toList();
        return ResponseEntity.ok(newsPaperDTOList);
    }

    @Operation(summary = "Save a newspaper using a request body.",
            description = "Save a newspaper using the request body, remember to check " +
                    "the schemas to understand and to save any newspaper you need a unique url.")
    @PostMapping("/save")
    public ResponseEntity<URI> save(@RequestBody @Valid NewsPaperDTO newsPaperDTO)
            throws URISyntaxException {
        if (newsPaperDTO.getSource() == null || newsPaperDTO.getSource().isBlank()) throw
                new SpecificException(HttpStatus.BAD_REQUEST, "The source parameter is required");
        if (newsPaperService.existsBySource(newsPaperDTO.getSource())) throw
                new SpecificException(HttpStatus.CONFLICT, "The Newspaper already exists");

        newsPaperService.save(NewsPaper.builder()
                .name(sourceName(newsPaperDTO.getSource())).source(newsPaperDTO.getSource())
                .progress("Not Initialized").total_sections(0).total_articles(0).build());

        Optional<NewsPaper> newsPaperOptional = newsPaperService.findBySource(newsPaperDTO.getSource());
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The Newspaper was not added");
        return ResponseEntity.created(new URI(String.
                format("/api/newspaper/find/%d", newsPaperOptional.get().getId()))).build();
    }

    @Operation(summary = "Update a newspaper using a id parameter and a request body.",
            description = "Update a newspaper using a id parameter and a request body, " +
                    "remember to check the schemas to understand and to save any " +
                    "newspaper you need a unique url.")
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateById(@PathVariable Long id,
                                             @RequestBody @Valid NewsPaperDTO newsPaperDTO) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The newspaper id does not exist");
        if (newsPaperDTO.getSource() == null || newsPaperDTO.getSource().isBlank()) throw
                new SpecificException(HttpStatus.BAD_REQUEST, "The source parameter is required");

        NewsPaper newsPaper = newsPaperOptional.get();
        newsPaper.setName(sourceName(newsPaperDTO.getSource()));
        newsPaper.setSource(newsPaperDTO.getSource());
        newsPaper.setProgress("Not Initialized");
        newsPaper.setTotal_sections(0);
        newsPaper.setTotal_articles(0);
        newsPaperService.save(newsPaper);
        return ResponseEntity.ok(String.format("Item with updated Id %d", id));
    }

    @Operation(summary = "Delete a newspaper using a id parameter.",
            description = "Delete a newspaper using a id parameter that exists.")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The newspaper id does not exist");

        newsPaperService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Find articles with a common word using a id parameter of a newspaper" +
            " and a word parameter from a newspaper.",
            description = "Find articles with a common word using a id parameter of a newspaper" +
                    " and a word parameter that exists from a newspaper, The word can be searched " +
                    "in the title, author and content.")
    @GetMapping("/contains/{id}/{word}")
    public ResponseEntity<NewsPaperDTO> containsByWord(@PathVariable Long id,
                                                       @PathVariable String word) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        if (word == null || word.isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The word parameter  required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The newspaper id does not exist");

        NewsPaper newsPaper = newsPaperOptional.get();
        NewsPaperDTO newsPaperDTO = NewsPaperDTO.builder()
                .id(newsPaper.getId()).name(newsPaper.getName()).source(newsPaper.getSource())
                .total_sections(newsPaper.getTotal_sections())
                .total_articles(newsPaper.getTotal_articles())
                .progress(newsPaper.getProgress()).articleList(newsPaper.getArticleList().stream()
                        .filter(article -> article.getTitle().contains(word) ||
                                article.getAuthors().contains(word) ||
                                article.getContent().contains(word)).toList()).build();
        return ResponseEntity.ok(newsPaperDTO);
    }

    @Operation(summary = "Find articles with a common word using a id parameter of a newspaper" +
            ", a word parameter and a request body with an article list.",
            description = "Find articles with a common word using a id parameter of a newspaper" +
                    ", a word parameter and a request body with an article list" +
                    " that exists, The word can be searched in the title, author and content " +
                    "that has been provided.")
    @GetMapping("/contains_re_find/{id}/{word}")
    public ResponseEntity<NewsPaperDTO> containsByWordWithBody(@PathVariable Long id,
                                                               @PathVariable String word,
                                                               @RequestBody @Valid
                                                               NewsPaperDTO newsPaperDTO) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        if (word == null || word.isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The word parameter  required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The newspaper id does not exist");

        if (newsPaperDTO.getArticleList().isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The list of newspaper articles is required");
        NewsPaperDTO newsPaperDTOSend = NewsPaperDTO.builder()
                .id(newsPaperDTO.getId()).name(newsPaperDTO.getName()).source(newsPaperDTO.getSource())
                .total_sections(newsPaperDTO.getTotal_sections())
                .total_articles(newsPaperDTO.getTotal_articles())
                .progress(newsPaperDTO.getProgress()).articleList(newsPaperDTO.getArticleList().stream()
                        .filter(article -> article.getTitle().contains(word) ||
                                article.getAuthors().contains(word) ||
                                article.getContent().contains(word)).toList()).build();
        return ResponseEntity.ok(newsPaperDTOSend);
    }

    @Operation(summary = "Send a request using an id parameter and \"y\" or \"n\" parameter " +
            "if you want to use a proxy.", description = "Send a request using an id parameter " +
            "and a \"y\" or \"n\" parameter if you want to use a proxy, at least one pattern " +
            "is required and a proxy if necessary, a process report will be returned, " +
            "an error may be returned of waiting time, however you can resend the request " +
            "and it is very possible to obtain a positive report, otherwise notify the administrator.")
    @GetMapping("/extract_articles_{proxyy}/{id}/")
    public ResponseEntity<String> extractArticles(@PathVariable Long id,
                                                        @PathVariable String proxyy)
            throws IOException {

        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        if (proxyy == null || proxyy.isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The type parameter is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The newspaper id does not exist");
        if (newsPaperOptional.get().getPatternList().isEmpty()) throw
                new SpecificException(HttpStatus.BAD_REQUEST, "At least one pattern is required");

        NewsPaper newsPaper = newsPaperOptional.get();

        if (proxyy.equals("y")) {
            return ResponseEntity.ok(managementService.extractAndBuild(newsPaper, true));
        } else if (proxyy.equals("n")) {
            return ResponseEntity.ok(managementService.extractAndBuild(newsPaper, false));
        } else throw new SpecificException(HttpStatus.BAD_REQUEST,
                "A parameter is wrong");
    }

    @Operation(summary = "Try to obtain the title of the newspaper with the id " +
            "parameter and if you want to use a proxy \"y\" or \"n\".",
            description = "Try to obtain the title of the newspaper with the id parameter " +
                    "and if you want to use a proxy \"y\" or \"n\", this operation does not " +
                    "save anything from the process, it requires that the newspaper have at " +
                    "least a proxy if you need it.")
    @GetMapping("/try_title_{proxyy}/{id}")
    public ResponseEntity<String> tryTitle(@PathVariable Long id, @PathVariable String proxyy)
            throws IOException {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        if (proxyy == null || proxyy.isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The type parameter is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The newspaper id does not exist");

        NewsPaper newsPaper = newsPaperOptional.get();
        if (proxyy.equals("y")) {
            return ResponseEntity.ok(testService.tryNewTitle(newsPaper, true));
        } else if (proxyy.equals("n")) {
            return ResponseEntity.ok(testService.tryNewTitle(newsPaper, false));
        } else throw new SpecificException(HttpStatus.BAD_REQUEST,
                "A parameter is wrong");
    }

    @Operation(summary = "Try to obtain a maximum of 10 sections of the newspaper with the id " +
            "parameter and if you want to use a proxy \"y\" or \"n\".",
            description = "Try to obtain a maximum of 10 sections of the newspaper with the id parameter " +
                    "and if you want to use a proxy \"y\" or \"n\", this operation does not " +
                    "save anything from the process, it requires that the newspaper have at " +
                    "least one matching pattern and a proxy if you need it.")
    @GetMapping("/try_sections_{proxyy}/{id}")
    public ResponseEntity<List<String>> trySections(@PathVariable Long id, @PathVariable String proxyy)
            throws IOException {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        if (proxyy == null || proxyy.isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The type parameter is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The newspaper id does not exist");
        if (newsPaperOptional.get().getPatternList().stream()
                .filter(pattern -> !pattern.getLoc_section().isBlank()).toList().isEmpty()) throw
                new SpecificException(HttpStatus.BAD_REQUEST,
                        "At least one pattern with sufficient data is required");

        NewsPaper newsPaper = newsPaperOptional.get();
        if (proxyy.equals("y")) {
            List<String> sections = testService.trySections(newsPaper, newsPaper
                            .getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_section().isBlank()).toList(),
                    true).stream().filter(each -> !each.isBlank()).toList();
            return ResponseEntity.ok(sections.size() > 10 ? sections.subList(0, 10) : sections);
        } else if (proxyy.equals("n")) {
            List<String> sections = testService.trySections(newsPaper, newsPaper
                            .getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_section().isBlank()).toList(),
                    false).stream().filter(each -> !each.isBlank()).toList();
            return ResponseEntity.ok(sections.size() > 10 ? sections.subList(0, 10) : sections);
        } else throw new SpecificException(HttpStatus.BAD_REQUEST,
                "A parameter is wrong");
    }

    @Operation(summary = "Try to obtain a maximum of 10 sources of articles of the " +
            "newspaper with the id " + "parameter and if you want to use a proxy \"y\" or \"n\".",
            description = "Try to obtain a maximum of 10 sources of articles of the newspaper " +
                    "with the id " + "parameter " +
                    "and if you want to use a proxy \"y\" or \"n\", this operation does not " +
                    "save anything from the process, it requires that the newspaper have at " +
                    "least one matching pattern and a proxy if you need it.")
    @GetMapping("/try_source_articles_{proxyy}/{id}")
    public ResponseEntity<List<String>> trySourceArticles(@PathVariable Long id,
                                                          @PathVariable String proxyy)
            throws IOException {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        if (proxyy == null || proxyy.isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The type parameter is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The newspaper id does not exist");
        if (newsPaperOptional.get().getPatternList().stream()
                .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                        !pattern.getLoc_per_article().isBlank()).toList().isEmpty()) throw
                new SpecificException(HttpStatus.BAD_REQUEST,
                        "At least one pattern with sufficient data is required");

        NewsPaper newsPaper = newsPaperOptional.get();
        if (proxyy.equals("y")) {
            List<String> sources = testService.tryArticlesSource(newsPaper,
                    newsPaper.getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                                    !pattern.getLoc_per_article().isBlank()).toList(),
                    true).stream().filter(each -> !each.isBlank()).toList();
            return ResponseEntity.ok(sources.size() > 10 ? sources.subList(0, 10) : sources);
        } else if (proxyy.equals("n")) {
            List<String> sources = testService.tryArticlesSource(newsPaper,
                    newsPaper.getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                                    !pattern.getLoc_per_article().isBlank()).toList(),
                    false).stream().filter(each -> !each.isBlank()).toList();
            return ResponseEntity.ok(sources.size() > 10 ? sources.subList(0, 10) : sources);
        } else throw new SpecificException(HttpStatus.BAD_REQUEST,
                "A parameter is wrong");
    }

    @Operation(summary = "Try to obtain a maximum of 10 titles of articles of the " +
            "newspaper with the id " + "parameter and if you want to use a proxy \"y\" or \"n\".",
            description = "Try to obtain a maximum of 10 titles of articles of the newspaper " +
                    "with the id " + "parameter " +
                    "and if you want to use a proxy \"y\" or \"n\", this operation does not " +
                    "save anything from the process, it requires that the newspaper have at " +
                    "least one matching pattern and a proxy if you need it.")
    @GetMapping("/try_article_title_{proxyy}/{id}")
    public ResponseEntity<List<String>> tryArticleTitle(@PathVariable Long id,
                                                        @PathVariable String proxyy)
            throws IOException {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        if (proxyy == null || proxyy.isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The type parameter is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The newspaper id does not exist");
        if (newsPaperOptional.get().getPatternList().stream()
                .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                        !pattern.getLoc_per_article().isBlank() &&
                        !pattern.getLoc_art_title().isBlank()).toList().isEmpty()) throw
                new SpecificException(HttpStatus.BAD_REQUEST,
                        "At least one pattern with sufficient data is required");

        NewsPaper newsPaper = newsPaperOptional.get();
        if (proxyy.equals("y")) {
            List<String> titles = testService.tryArticleTitle(newsPaper, newsPaper
                            .getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                                    !pattern.getLoc_per_article().isBlank() &&
                                    !pattern.getLoc_art_title().isBlank()).toList(),
                    true).stream().filter(each -> !each.isBlank()).toList();
            return ResponseEntity.ok(titles.size() > 10 ? titles.subList(0, 10) : titles);
        } else if (proxyy.equals("n")) {
            List<String> titles = testService.tryArticleTitle(newsPaper, newsPaper
                            .getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                                    !pattern.getLoc_per_article().isBlank() &&
                                    !pattern.getLoc_art_title().isBlank()).toList(),
                    false).stream().filter(each -> !each.isBlank()).toList();
            return ResponseEntity.ok(titles.size() > 10 ? titles.subList(0, 10) : titles);
        } else throw new SpecificException(HttpStatus.BAD_REQUEST,
                "A parameter is wrong");
    }

    @Operation(summary = "Try to obtain a maximum of 10 authors of articles of the " +
            "newspaper with the id " + "parameter and if you want to use a proxy \"y\" or \"n\".",
            description = "Try to obtain a maximum of 10 authors of articles of the newspaper " +
                    "with the id " + "parameter " +
                    "and if you want to use a proxy \"y\" or \"n\", this operation does not " +
                    "save anything from the process, it requires that the newspaper have at " +
                    "least one matching pattern and a proxy if you need it.")
    @GetMapping("/try_article_authors_{proxyy}/{id}")
    public ResponseEntity<List<String>> tryArticleAuthors(@PathVariable Long id,
                                                          @PathVariable String proxyy)
            throws IOException {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        if (proxyy == null || proxyy.isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The type parameter is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The newspaper id does not exist");
        if (newsPaperOptional.get().getPatternList().stream()
                .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                        !pattern.getLoc_per_article().isBlank() &&
                        !pattern.getLoc_art_authors().isBlank()).toList().isEmpty()) throw
                new SpecificException(HttpStatus.BAD_REQUEST,
                        "At least one pattern with sufficient data is required");

        NewsPaper newsPaper = newsPaperOptional.get();
        if (proxyy.equals("y")) {
            List<String> authors = testService.tryArticleAuthors(newsPaper, newsPaper
                            .getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                                    !pattern.getLoc_per_article().isBlank() &&
                                    !pattern.getLoc_art_authors().isBlank()).toList(),
                    true).stream().filter(each -> !each.isBlank()).toList();
            return ResponseEntity.ok(authors.size() > 10 ? authors.subList(0, 10) : authors);
        } else if (proxyy.equals("n")) {
            List<String> authors = testService.tryArticleAuthors(newsPaper, newsPaper
                            .getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                                    !pattern.getLoc_per_article().isBlank() &&
                                    !pattern.getLoc_art_authors().isBlank()).toList(),
                    false).stream().filter(each -> !each.isBlank()).toList();
            return ResponseEntity.ok(authors.size() > 10 ? authors.subList(0, 10) : authors);
        } else throw new SpecificException(HttpStatus.BAD_REQUEST,
                "A parameter is wrong");
    }

    @Operation(summary = "Try to obtain a maximum of 10 contents of articles of the " +
            "newspaper with the id " + "parameter and if you want to use a proxy \"y\" or \"n\".",
            description = "Try to obtain a maximum of 10 contents of articles of the newspaper " +
                    "with the id " + "parameter " +
                    "and if you want to use a proxy \"y\" or \"n\", this operation does not " +
                    "save anything from the process, it requires that the newspaper have at " +
                    "least one matching pattern and a proxy if you need it.")
    @GetMapping("/try_article_content_{proxyy}/{id}")
    public ResponseEntity<List<String>> tryArticleContent(@PathVariable Long id,
                                                          @PathVariable String proxyy)
            throws IOException {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        if (proxyy == null || proxyy.isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The type parameter is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The newspaper id does not exist");
        if (newsPaperOptional.get().getPatternList().stream()
                .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                        !pattern.getLoc_per_article().isBlank() &&
                        !pattern.getLoc_art_content().isBlank()).toList().isEmpty()) throw
                new SpecificException(HttpStatus.BAD_REQUEST,
                        "At least one pattern with sufficient data is required");

        NewsPaper newsPaper = newsPaperOptional.get();
        if (proxyy.equals("y")) {
            List<String> contents = testService.tryArticleContent(newsPaper, newsPaper
                            .getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                                    !pattern.getLoc_per_article().isBlank() &&
                                    !pattern.getLoc_art_content().isBlank()).toList(),
                    true).stream().filter(each -> !each.isBlank()).toList();
            return ResponseEntity.ok(contents.size() > 10 ? contents.subList(0, 10) : contents);
        } else if (proxyy.equals("n")) {
            List<String> contents = testService.tryArticleContent(newsPaper, newsPaper
                            .getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                                    !pattern.getLoc_per_article().isBlank() &&
                                    !pattern.getLoc_art_content().isBlank()).toList(),
                    false).stream().filter(each -> !each.isBlank()).toList();
            return ResponseEntity.ok(contents.size() > 10 ? contents.subList(0, 10) : contents);
        } else throw new SpecificException(HttpStatus.BAD_REQUEST,
                "A parameter is wrong");
    }

    @Operation(summary = "Tests the proxies and saves their " +
            "states using an id parameter from an existing newspaper.",
            description = "Tests the proxies and saves their states using an id " +
                    "parameter from an existing periodic, if it can return a connection " +
                    "timeout error, check or change the proxies, ultimately remember " +
                    "that each proxy is tested with the source of the newspaper, so it is " +
                    "the source who does not accept proxies.")
    @GetMapping("/check_proxies/{id}")
    public ResponseEntity<NewsPaperDTO> checkProxies(@PathVariable Long id) throws IOException {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The newspaper id does not exist");
        if(newsPaperOptional.get().getProxyyList().isEmpty())
            throw new SpecificException(HttpStatus.NOT_FOUND,
                    "The proxy list of the newspaper is empty");
        proxyyService.checkProxyList(newsPaperOptional.get().getSource(),
                newsPaperOptional.get().getProxyyList());
        NewsPaper newsPaper = newsPaperOptional.get();
        NewsPaperDTO newsPaperDTO = NewsPaperDTO.builder()
                .id(newsPaper.getId()).name(newsPaper.getName())
                .source(newsPaper.getSource()).total_sections(newsPaper.getTotal_sections())
                .total_articles(newsPaper.getTotal_articles()).progress(newsPaper.getProgress())
                .proxyyList(newsPaper.getProxyyList()).build();
        return ResponseEntity.ok(newsPaperDTO);
    }

    @Operation(summary = "Clear the list of patterns from a newspaper using an " +
            "existing id parameter", description = "Clear the list of patterns " +
            "from a newspaper using an existing id parameter.")
    @GetMapping("/clean_patterns/{id}")
    public ResponseEntity<NewsPaperDTO> cleanPatterns(@PathVariable Long id) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The newspaper id does not exist");

        NewsPaper newsPaper = newsPaperOptional.get();
        newsPaperService.cleanPatterns(newsPaper);
        NewsPaperDTO newsPaperDTO = NewsPaperDTO.builder()
                .id(newsPaper.getId()).name(newsPaper.getName())
                .source(newsPaper.getSource()).total_sections(newsPaper.getTotal_sections())
                .total_articles(newsPaper.getTotal_articles()).progress(newsPaper.getProgress())
                .patternList(newsPaper.getPatternList()).sectionList(newsPaper.getSectionList())
                .articleList(newsPaper.getArticleList())
                .proxyyList(newsPaper.getProxyyList()).build();
        return ResponseEntity.ok(newsPaperDTO);
    }

    @Operation(summary = "Clear the list of proxies from a newspaper using an " +
            "existing id parameter", description = "Clear the list of proxies " +
            "from a newspaper using an existing id parameter.")
    @GetMapping("/clean_proxy_list/{id}")
    public ResponseEntity<NewsPaperDTO> cleanProxyyList(@PathVariable Long id) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The newspaper id does not exist");

        NewsPaper newsPaper = newsPaperOptional.get();
        newsPaperService.cleanProxyList(newsPaper);
        NewsPaperDTO newsPaperDTO = NewsPaperDTO.builder()
                .id(newsPaper.getId()).name(newsPaper.getName())
                .source(newsPaper.getSource()).total_sections(newsPaper.getTotal_sections())
                .total_articles(newsPaper.getTotal_articles()).progress(newsPaper.getProgress())
                .patternList(newsPaper.getPatternList()).sectionList(newsPaper.getSectionList())
                .articleList(newsPaper.getArticleList())
                .proxyyList(newsPaper.getProxyyList()).build();
        return ResponseEntity.ok(newsPaperDTO);
    }

    @Operation(summary = "Clear the list of sections from a newspaper using an " +
            "existing id parameter", description = "Clear the list of sections " +
            "from a newspaper using an existing id parameter.")
    @GetMapping("/clean_sections/{id}")
    public ResponseEntity<NewsPaperDTO> cleanSections(@PathVariable Long id) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The newspaper id does not exist");

        NewsPaper newsPaper = newsPaperOptional.get();
        newsPaperService.cleanSections(newsPaper);
        NewsPaperDTO newsPaperDTO = NewsPaperDTO.builder()
                .id(newsPaper.getId()).name(newsPaper.getName())
                .source(newsPaper.getSource()).total_sections(newsPaper.getTotal_sections())
                .total_articles(newsPaper.getTotal_articles()).progress(newsPaper.getProgress())
                .patternList(newsPaper.getPatternList()).sectionList(newsPaper.getSectionList())
                .articleList(newsPaper.getArticleList())
                .proxyyList(newsPaper.getProxyyList()).build();
        return ResponseEntity.ok(newsPaperDTO);
    }

    @Operation(summary = "Clear the list of articles from a newspaper using an " +
            "existing id parameter", description = "Clear the list of articles " +
            "from a newspaper using an existing id parameter.")
    @GetMapping("/clean_articles/{id}")
    public ResponseEntity<NewsPaperDTO> cleanArticles(@PathVariable Long id) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The newspaper id does not exist");

        NewsPaper newsPaper = newsPaperOptional.get();
        newsPaperService.cleanArticles(newsPaper);
        NewsPaperDTO newsPaperDTO = NewsPaperDTO.builder()
                .id(newsPaper.getId()).name(newsPaper.getName())
                .source(newsPaper.getSource()).total_sections(newsPaper.getTotal_sections())
                .total_articles(newsPaper.getTotal_articles()).progress(newsPaper.getProgress())
                .patternList(newsPaper.getPatternList()).sectionList(newsPaper.getSectionList())
                .articleList(newsPaper.getArticleList())
                .proxyyList(newsPaper.getProxyyList()).build();
        return ResponseEntity.ok(newsPaperDTO);
    }

    @Operation(summary = "Clear the list of articles without the source from a newspaper using an " +
            "existing id parameter", description = "Clear the list of articles without the source " +
            "from a newspaper using an existing id parameter.")
    @GetMapping("/clean_articles_no_sources/{id}")
    public ResponseEntity<NewsPaperDTO> cleanArticlesWithoutSource(@PathVariable Long id) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(id);
        if (newsPaperOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The newspaper id does not exist");

        NewsPaper newsPaper = newsPaperOptional.get();
        newsPaperService.cleanArticlesWithOutSource(newsPaper);
        NewsPaperDTO newsPaperDTO = NewsPaperDTO.builder()
                .id(newsPaper.getId()).name(newsPaper.getName())
                .source(newsPaper.getSource()).total_sections(newsPaper.getTotal_sections())
                .total_articles(newsPaper.getTotal_articles()).progress(newsPaper.getProgress())
                .patternList(newsPaper.getPatternList()).sectionList(newsPaper.getSectionList())
                .articleList(newsPaper.getArticleList())
                .proxyyList(newsPaper.getProxyyList()).build();
        return ResponseEntity.ok(newsPaperDTO);
    }

    public String sourceName(String source) {
        String host = URI.create(source).getHost();
        return host.startsWith("www.") ? host.substring(4, host.lastIndexOf(".")) :
                host.substring(0, host.lastIndexOf("."));
    }
}