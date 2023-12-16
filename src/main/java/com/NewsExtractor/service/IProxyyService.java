package com.NewsExtractor.service;

import com.NewsExtractor.entity.Proxyy;

import java.io.IOException;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;

public interface IProxyyService {
    List<Proxyy> findAll();

    Optional<Proxyy> findById(Long id);

    Optional<Proxyy> findByName(String name);

    boolean existsByName(String name);

    void save(Proxyy proxyy);

    void deleteById(Long id);

    String checkProxy(String source, Proxyy proxyy) throws IOException;

    String checkProxyList(String source, List<Proxyy> proxyyList) throws IOException;

    List<Proxyy> usefulProxy(List<Proxyy> proxyyList) throws IOException;

    Proxy returnOne(List<Proxyy> proxyList);

}
