package com.NewsExtractor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String source;
    @Column(nullable = false)
    private Integer total_articles;

    @ManyToOne
    @JoinColumn(name = "id_newspaper", nullable = false)
    @JsonIgnore
    private NewsPaper newsPaper;

    @OneToMany(mappedBy = "section", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Article> articleList = new ArrayList<>();

}
