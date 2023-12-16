package com.NewsExtractor.controller;

import com.NewsExtractor.dto.SectionDTO;
import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.entity.Section;
import com.NewsExtractor.exception.SpecificException;
import com.NewsExtractor.service.INewsPaperService;
import com.NewsExtractor.service.ISectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/section")
@Tag(name = "Section Controller", description = "Manage all items accordingly.")
public class SectionController {
    @Autowired
    private ISectionService sectionService;

    @Autowired
    private INewsPaperService newsPaperService;

    @Operation(summary = "Find an section using a id parameter.",
            description = "Find an section using a id parameter that exists.")
    @GetMapping("/find/{id}")
    public ResponseEntity<SectionDTO> findById(@PathVariable Long id) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<Section> sectionOptional = sectionService.findById(id);
        if (sectionOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The section id does not exist");

        Section section = sectionOptional.get();
        SectionDTO sectionDTO = SectionDTO.builder().id(section.getId())
                .name(section.getName()).source(section.getSource())
                .total_articles(section.getTotal_articles()).newsPaper(section.getNewsPaper())
                .articleList(section.getArticleList()).build();
        return ResponseEntity.ok(sectionDTO);
    }

    @Operation(summary = "Find all the sections.",
            description = "Find all the sections that exists.")
    @GetMapping("/findAll")
    public ResponseEntity<List<SectionDTO>> findAll() {
        List<SectionDTO> sectionDTOList = sectionService.findAll().stream()
                .map(section -> SectionDTO.builder().id(section.getId())
                        .name(section.getName()).source(section.getSource())
                        .total_articles(section.getTotal_articles())
                        .newsPaper(section.getNewsPaper()).articleList(section.getArticleList())
                        .build()).toList();
        return ResponseEntity.ok(sectionDTOList);
    }

    @Operation(summary = "Save an section using a request body.",
            description = "Save an section using the request body, remember to check " +
                    "the schemas to understand and to save any article you need newspaper " +
                    "object with a unique identifier.")
    @PostMapping("/save")
    public ResponseEntity<URI> save(@RequestBody @Valid SectionDTO sectionDTO)
            throws URISyntaxException {
        if (sectionDTO.getNewsPaper().getId() == null ||
                sectionDTO.getNewsPaper().getId().toString().isBlank()) throw
                new SpecificException(HttpStatus.BAD_REQUEST, "At least the newspaper id is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(sectionDTO
                .getNewsPaper().getId());
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The newspaper to be related to the section does not exist");
        if (sectionDTO.getSource() == null || sectionDTO.getSource().isBlank())
            throw new SpecificException(HttpStatus.BAD_REQUEST,
                    "The source of the section is required");
        if (sectionService.existsBySource(sectionDTO.getSource())) throw
                new SpecificException(HttpStatus.CONFLICT, "The section already exists");

        sectionService.save(Section.builder().source(sectionDTO.getSource())
                .total_articles(0).newsPaper(sectionDTO.getNewsPaper()).build());

        Optional<Section> sectionOptional = sectionService.findBySource(sectionDTO.getSource());
        if (sectionOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The section was not added");
        return ResponseEntity.created(new URI(String.format("/api/section/find/%d",
                sectionOptional.get().getId()))).build();
    }

    @Operation(summary = "Update an article using a id parameter and a request body.",
            description = "Update an article using a id parameter and a request body, " +
                    "remember to check the schemas to understand and to save any article " +
                    "you need newspaper object with a unique " +
                    "identifier.")
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateById(@PathVariable Long id,
                                             @RequestBody @Valid SectionDTO sectionDTO) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        if (sectionDTO.getNewsPaper().getId() == null ||
                sectionDTO.getNewsPaper().getId().toString().isBlank()) throw
                new SpecificException(HttpStatus.BAD_REQUEST, "At least the newspaper id is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(sectionDTO
                .getNewsPaper().getId());
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The newspaper to be related to the section does not exist");
        if (sectionDTO.getSource() == null || sectionDTO.getSource().isBlank())
            throw new SpecificException(HttpStatus.BAD_REQUEST,
                    "The source of the section is required");
        if (sectionService.existsBySource(sectionDTO.getSource())) throw
                new SpecificException(HttpStatus.CONFLICT, "The section already exists");

        Optional<Section> sectionOptional = sectionService.findById(id);
        if (sectionOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The section id does not exist");

        Section section = sectionOptional.get();
        section.setSource(sectionDTO.getSource());
        section.setTotal_articles(0);
        section.setNewsPaper(sectionDTO.getNewsPaper());
        sectionService.save(section);
        return ResponseEntity.ok(String.format("Item with updated Id %d", id));
    }

    @Operation(summary = "Delete an section using a id parameter.",
            description = "Delete an section using a id parameter that exists.")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        if (id == null) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<Section> sectionOptional = sectionService.findById(id);
        if (sectionOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The section id does not exist");

        sectionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
