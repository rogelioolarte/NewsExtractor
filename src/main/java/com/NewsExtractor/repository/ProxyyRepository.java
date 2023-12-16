package com.NewsExtractor.repository;

import com.NewsExtractor.entity.Proxyy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ProxyyRepository extends JpaRepository<Proxyy, Long> {
    Optional<Proxyy> findByName(String name);

    boolean existsByName(String name);
}
