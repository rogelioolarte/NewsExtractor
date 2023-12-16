package com.NewsExtractor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "patterns")
public class Pattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false)
    private String loc_section;
    @Column(nullable = false)
    private String loc_per_article;
    @Column(nullable = false)
    private String loc_art_title;
    @Column(nullable = false)
    private String loc_art_authors;
    @Column(nullable = false)
    private String loc_art_content;

    @ManyToOne
    @JoinColumn(name = "id_newspaper", nullable = false)
    @JsonIgnore
    private NewsPaper newsPaper;
}
