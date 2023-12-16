package com.NewsExtractor.controller;

import com.NewsExtractor.dto.PatternDTO;
import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.entity.Pattern;
import com.NewsExtractor.exception.SpecificException;
import com.NewsExtractor.service.INewsPaperService;
import com.NewsExtractor.service.IPatternService;
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
@RequestMapping("/api/pattern")
@Tag(name = "Pattern Controller", description = "Manage all items accordingly.")
public class PatternController {

    @Autowired
    private IPatternService patternService;

    @Autowired
    private INewsPaperService newsPaperService;

    @Operation(summary = "Find an pattern using a id parameter.",
            description = "Find an pattern using a id parameter that exists.")
    @GetMapping("/find/{id}")
    public ResponseEntity<PatternDTO> findById(@PathVariable Long id) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<Pattern> patternOptional = patternService.findById(id);
        if (patternOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The pattern id does not exist");

        Pattern pattern = patternOptional.get();
        PatternDTO patternDTO = PatternDTO.builder()
                .id(pattern.getId()).name(pattern.getName())
                .loc_section(pattern.getLoc_section()).loc_per_article(pattern.getLoc_per_article())
                .loc_art_title(pattern.getLoc_art_title()).loc_art_authors(pattern.getLoc_art_authors())
                .loc_art_content(pattern.getLoc_art_content())
                .newsPaper(pattern.getNewsPaper()).build();
        return ResponseEntity.ok(patternDTO);
    }

    @Operation(summary = "Find all the patterns.",
            description = "Find all the patterns that exists.")
    @GetMapping("/findAll")
    public ResponseEntity<List<PatternDTO>> findAll() {
        List<PatternDTO> patternDTOList = patternService.findAll().stream()
                .map(pattern -> PatternDTO.builder()
                        .id(pattern.getId())
                        .name(pattern.getName())
                        .loc_section(pattern.getLoc_section())
                        .loc_per_article(pattern.getLoc_per_article())
                        .loc_art_title(pattern.getLoc_art_title())
                        .loc_art_authors(pattern.getLoc_art_authors())
                        .loc_art_content(pattern.getLoc_art_content())
                        .newsPaper(pattern.getNewsPaper())
                        .build())
                .toList();
        return ResponseEntity.ok(patternDTOList);
    }

    @Operation(summary = "Save an pattern using a request body.",
            description = "Save an pattern using the request body, remember to check " +
                    "the schemas to understand and to save any article you need newspaper " +
                    "object with a unique identifier.")
    @PostMapping("/save")
    public ResponseEntity<URI> save(@RequestBody PatternDTO patternDTO)
            throws URISyntaxException {
        if (patternDTO.getNewsPaper().getId() == null ||
                patternDTO.getNewsPaper().getId().toString().isBlank())
            throw new SpecificException(HttpStatus.BAD_REQUEST, "At least the newspaper id is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(patternDTO
                .getNewsPaper().getId());
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The newspaper to be related to the pattern does not exist");
        if (patternDTO.getLoc_section() == null || patternDTO.getLoc_per_article() == null ||
                patternDTO.getLoc_art_title() == null ||
                patternDTO.getLoc_art_authors() == null ||
                patternDTO.getLoc_art_content() == null) throw
                new SpecificException(HttpStatus.BAD_REQUEST,
                        "No field must be null");
        if (patternDTO.getLoc_section().isBlank() && patternDTO.getLoc_per_article().isBlank() &&
                patternDTO.getLoc_art_title().isBlank() &&
                patternDTO.getLoc_art_authors().isBlank() &&
                patternDTO.getLoc_art_content().isBlank()) throw
                new SpecificException(HttpStatus.BAD_REQUEST,
                        "At least one specific piece of information is required");
        String newName = createName(newsPaperOptional.get(), 1);
        if (patternService.existsByName(newName)) throw new SpecificException(HttpStatus.CONFLICT,
                "The pattern already exists");

        patternService.save(Pattern.builder()
                .name(newName).loc_section(patternDTO.getLoc_section())
                .loc_per_article(patternDTO.getLoc_per_article())
                .loc_art_title(patternDTO.getLoc_art_title())
                .loc_art_authors(patternDTO.getLoc_art_authors())
                .loc_art_content(patternDTO.getLoc_art_content())
                .newsPaper(patternDTO.getNewsPaper()).build());

        Optional<Pattern> patternOptional = patternService
                .findByName(createName(newsPaperOptional.get(), 0));
        if (patternOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The pattern was not added");
        return ResponseEntity.created(new URI(String.
                format("/api/pattern/find/%d", patternOptional.get().getId()))).build();
    }

    @Operation(summary = "Update an pattern using a id parameter and a request body.",
            description = "Update an pattern using a id parameter and a request body, " +
                    "remember to check the schemas to understand and to save any article " +
                    "you need newspaper object with a unique identifier.")
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateById(@PathVariable Long id,
                                             @RequestBody PatternDTO patternDTO) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        if (patternDTO.getNewsPaper().getId() == null ||
                patternDTO.getNewsPaper().getId().toString().isBlank())
            throw new SpecificException(HttpStatus.BAD_REQUEST, "At least the newspaper id is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(patternDTO
                .getNewsPaper().getId());
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The newspaper to be related to the pattern does not exist");
        if (patternDTO.getLoc_section() == null || patternDTO.getLoc_per_article() == null ||
                patternDTO.getLoc_art_title() == null ||
                patternDTO.getLoc_art_authors() == null ||
                patternDTO.getLoc_art_content() == null) throw
                new SpecificException(HttpStatus.BAD_REQUEST,
                        "No field must be null");
        if (patternDTO.getLoc_section().isBlank() && patternDTO.getLoc_per_article().isBlank() &&
                patternDTO.getLoc_art_title().isBlank() &&
                patternDTO.getLoc_art_authors().isBlank() &&
                patternDTO.getLoc_art_content().isBlank()) throw
                new SpecificException(HttpStatus.BAD_REQUEST,
                        "At least one specific piece of information is required");
        Optional<Pattern> patternOptional = patternService.findById(id);
        if (patternOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The pattern id does not exist");

        Pattern pattern = patternOptional.get();
        pattern.setLoc_section(patternDTO.getLoc_section());
        pattern.setLoc_per_article(patternDTO.getLoc_per_article());
        pattern.setLoc_art_title(patternDTO.getLoc_art_title());
        pattern.setLoc_art_authors(patternDTO.getLoc_art_authors());
        pattern.setLoc_art_content(patternDTO.getLoc_art_content());
        pattern.setNewsPaper(patternDTO.getNewsPaper());
        patternService.save(pattern);
        return ResponseEntity.ok(String.format("Item with updated Id %d", id));
    }

    @Operation(summary = "Delete an pattern using a id parameter.",
            description = "Delete an pattern using a id parameter that exists.")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        if (id == null) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<Pattern> patternOptional = patternService.findById(id);
        if (patternOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The pattern id does not exist");

        patternService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public String createName(NewsPaper newsPaper, Integer addTo) {
        return "pattern_".concat(String.valueOf(newsPaper.getPatternList().size() + addTo));
    }
}
