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
@Table(name = "proxies")
public class Proxyy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private Integer port;
    @Column(nullable = false)
    private Integer state;

    @ManyToOne
    @JoinColumn(name = "id_newspaper", nullable = false)
    @JsonIgnore
    private NewsPaper newsPaper;
}
