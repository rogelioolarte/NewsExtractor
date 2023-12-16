package com.NewsExtractor.service.Impl;

import com.NewsExtractor.entity.Proxyy;
import com.NewsExtractor.persistence.IProxyyDAO;
import com.NewsExtractor.service.IProxyyService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;

@Service
public class ProxyyServiceImpl implements IProxyyService {

    private final String generalAgentToUse = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 " +
            "Safari/537.36";
    private final Connection session = Jsoup.newSession()
            .userAgent(generalAgentToUse).ignoreHttpErrors(true);
    @Autowired
    private IProxyyDAO proxyyDAO;

    @Override
    public List<Proxyy> findAll() {
        return proxyyDAO.findAll();
    }

    @Override
    public Optional<Proxyy> findById(Long id) {
        return proxyyDAO.findById(id);
    }

    @Override
    public Optional<Proxyy> findByName(String name) {
        return proxyyDAO.findByName(name);
    }

    @Override
    public boolean existsByName(String name) {
        return proxyyDAO.existsByName(name);
    }

    @Override
    public void save(Proxyy proxy) {
        proxyyDAO.save(proxy);
    }

    @Override
    public void deleteById(Long id) {
        proxyyDAO.deleteById(id);
    }

    @Override
    public String checkProxy(String source, Proxyy proxyy) throws IOException {
        Connection.Response response = session.newRequest(source)
                .proxy(new Proxy(createType(proxyy.getType()),
                        new InetSocketAddress(proxyy.getAddress(), proxyy.getPort()))).execute();
        System.out.println(response.statusCode());
        proxyy.setState(response.statusCode());
        save(proxyy);
        return "Proxy check completed";
    }

    @Override
    public String checkProxyList(String source, List<Proxyy> proxyyList) throws IOException {
        if (proxyyList.size() > 1) {
            for (Proxyy proxy : proxyyList) {
                Connection.Response response = Jsoup.connect(source)
                        .proxy(new Proxy(createType(proxy.getType()),
                                new InetSocketAddress(proxy.getAddress(), proxy.getPort())))
                        .ignoreHttpErrors(true).execute();
                proxy.setState(response.statusCode());
                save(proxy);
            }
            return "Proxy check completed";
        } else if (proxyyList.stream().findFirst().isPresent()) {
            Proxyy proxyy = proxyyList.stream().findFirst().get();
            Connection.Response response = Jsoup.connect(source)
                    .proxy(new Proxy(createType(proxyy.getType()),
                            new InetSocketAddress(proxyy.getAddress(),
                                    proxyy.getPort())))
                    .ignoreHttpErrors(true).execute();
            proxyy.setState(response.statusCode());
            save(proxyy);
            return "Proxy check completed";
        } else {
            return "There is no list of proxies";
        }
    }

    @Override
    public List<Proxyy> usefulProxy(List<Proxyy> proxyyList) {
        return proxyyList.stream().filter(proxyy -> proxyy.getState() == 200).toList();
    }

    @Override
    public Proxy returnOne(List<Proxyy> proxyyList) {
        return proxyyList.stream()
                .findFirst().isPresent() ?
                new Proxy(createType(proxyyList.stream().findFirst().get().getType()),
                        new InetSocketAddress(proxyyList.stream().findFirst().get().getAddress(),
                                proxyyList.stream().findFirst().get().getPort())) : null;
    }

    public Proxy.Type createType(String string) {
        return switch (string) {
            case "DIRECT" -> Proxy.Type.DIRECT;
            case "HTTP" -> Proxy.Type.HTTP;
            case "SOCKS" -> Proxy.Type.SOCKS;
            default -> Proxy.NO_PROXY.type();
        };
    }

}
