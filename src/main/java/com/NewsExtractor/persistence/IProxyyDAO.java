package com.NewsExtractor.persistence;

import com.NewsExtractor.entity.Proxyy;

import java.util.List;
import java.util.Optional;

public interface IProxyyDAO {
    List<Proxyy> findAll();

    Optional<Proxyy> findById(Long id);

    Optional<Proxyy> findByName(String name);

    boolean existsByName(String name);

    void save(Proxyy Proxyy);

    void deleteById(Long id);
}
