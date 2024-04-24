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
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 400, nullable = false)
    private String title;
    @Column(length = 400, unique = true, nullable = false)
    private String source;
    @Column(nullable = false)
    private String authors;
    @Column(columnDefinition = "TEXT", length = 50000, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "id_newspaper", nullable = false)
    @JsonIgnore
    private NewsPaper newsPaper;

    @ManyToOne
    @JoinColumn(name = "id_section", nullable = false)
    @JsonIgnore
    private Section section;
}
