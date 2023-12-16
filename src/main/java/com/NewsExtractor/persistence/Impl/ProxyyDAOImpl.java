package com.NewsExtractor.persistence.Impl;

import com.NewsExtractor.entity.Proxyy;
import com.NewsExtractor.persistence.IProxyyDAO;
import com.NewsExtractor.repository.ProxyyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProxyyDAOImpl implements IProxyyDAO {

    @Autowired
    private ProxyyRepository proxyyRepository;

    @Override
    public List<Proxyy> findAll() {
        return proxyyRepository.findAll();
    }

    @Override
    public Optional<Proxyy> findById(Long id) {
        return proxyyRepository.findById(id);
    }

    @Override
    public Optional<Proxyy> findByName(String name) {
        return proxyyRepository.findByName(name);
    }

    @Override
    public boolean existsByName(String name) {
        return proxyyRepository.existsByName(name);
    }

    @Override
    public void save(Proxyy proxyy) {
        proxyyRepository.save(proxyy);
    }

    @Override
    public void deleteById(Long id) {
        proxyyRepository.deleteById(id);
    }

}
