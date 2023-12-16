package com.NewsExtractor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "newspapers")
public class NewsPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String source;
    @Column(nullable = false)
    private Integer total_sections;
    @Column(nullable = false)
    private Integer total_articles;
    @Column(nullable = false)
    private String progress;

    @OneToMany(mappedBy = "newsPaper", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Pattern> patternList = new ArrayList<>();

    @OneToMany(mappedBy = "newsPaper", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Section> sectionList = new ArrayList<>();

    @OneToMany(mappedBy = "newsPaper", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Article> articleList = new ArrayList<>();

    @OneToMany(mappedBy = "newsPaper", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Proxyy> proxyyList = new ArrayList<>();
}
