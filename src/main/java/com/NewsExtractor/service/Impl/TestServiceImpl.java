package com.NewsExtractor.service.Impl;

import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.entity.Pattern;
import com.NewsExtractor.entity.Proxyy;
import com.NewsExtractor.service.IProxyyService;
import com.NewsExtractor.service.ITestService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestServiceImpl implements ITestService {

    private final String generalAgentToUse = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 " +
            "Safari/537.36";
    private final Connection session = Jsoup.newSession()
            .userAgent(generalAgentToUse).ignoreHttpErrors(true);
    @Autowired
    private IProxyyService proxyyService;

    public Connection.Response obtainResponse(String source, List<Proxyy> proxyyList,
                                              Boolean useProxyy) throws IOException {
        if (useProxyy && !proxyyList.isEmpty()) {
            return session.newRequest(source).userAgent(generalAgentToUse)
                    .proxy(proxyyService.returnOne(proxyyList)).execute();
        }
        return session.newRequest(source).userAgent(generalAgentToUse).execute();
    }

    public String sourceName(String source) {
        String host = URI.create(source).getHost();
        return host.startsWith("www.") ? host.substring(4, host.lastIndexOf(".")) :
                host.substring(0, host.lastIndexOf("."));
    }

    @Override
    public String reCheckSource(String source) {
        return source.endsWith("/") ? source.substring(0, source.lastIndexOf("/")) :
                source;
    }

    @Override
    public String tryNewTitle(NewsPaper newsPaper, Boolean useProxyy) throws IOException {
        Connection.Response response = obtainResponse(reCheckSource(newsPaper.getSource()),
                newsPaper.getProxyyList(), useProxyy);
        if (response.statusCode() >= 400) {
            return "Error connecting...";
        }
        Document website = response.parse();
        if (website.title().length() < 40) {
            return website.title();
        }
        return "Title too long, It will set the default title.";
    }

    @Override
    public List<String> trySections(NewsPaper newsPaper, List<Pattern> patterns, Boolean useProxyy)
            throws IOException {
        Document website = obtainResponse(reCheckSource(newsPaper.getSource()),
                newsPaper.getProxyyList(), useProxyy).parse();
        List<String> sectionsList = new ArrayList<>();
        if (patterns.size() > 1) {
            for (Pattern pattern : patterns) {
                Elements sectionList = website.select(pattern.getLoc_section());
                for (Element section : sectionList) {
                    if (section.attr("href").startsWith("/")) {
                        sectionsList.add(reCheckSource(newsPaper.getSource()).
                                concat(section.attr("href")));
                    } else if (section.attr("href")
                            .contains(newsPaper.getSource())) {
                        sectionsList.add(section.attr("href"));
                    }
                }
            }
            return sectionsList;
        } else if (patterns.stream().findFirst().isPresent()) {
            Elements sectionList = website.select(patterns.stream()
                    .findFirst().get().getLoc_section());
            for (Element section : sectionList) {
                if (section.attr("href").startsWith("/")) {
                    sectionsList.add(reCheckSource(newsPaper.getSource()).
                            concat(section.attr("href")));
                } else if (section.attr("href")
                        .contains(newsPaper.getSource())) {
                    sectionsList.add(section.attr("href"));
                }
            }
            return sectionsList;
        }
        sectionsList.add("The pattern(s) didn't extract anything.");
        return sectionsList;
    }

    @Override
    public List<String> tryArticlesSource(NewsPaper newsPaper, List<Pattern> patterns,
                                          Boolean useProxyy) throws IOException {
        List<String> articlesList = new ArrayList<>();
        List<String> sectionsList = trySections(newsPaper, newsPaper
                        .getPatternList().stream()
                        .filter(pattern -> !pattern.getLoc_section().isBlank()).toList(),
                useProxyy).stream().filter(each -> !each.isBlank()).toList();

        if (sectionsList.isEmpty() || sectionsList.stream().findFirst().get()
                .equals("The pattern(s) didn't extract anything.")) {
            articlesList.add("The sections of this newspaper is empty " +
                    "and It's not possible continue the test.");
            return articlesList;
        }

        for (String section : sectionsList.size() > 10 ? sectionsList.subList(0, 10) : sectionsList) {
            Document website_sec = obtainResponse(reCheckSource(section),
                    newsPaper.getProxyyList(), useProxyy).parse();
            if (patterns.size() > 1) {
                for (Pattern pattern : patterns) {
                    Elements articleList = website_sec.select(pattern.
                            getLoc_per_article());
                    for (Element article : articleList) {
                        if (article.attr("href").startsWith("/")) {
                            articlesList.add(reCheckSource(newsPaper.getSource())
                                    .concat(article.attr("href")));
                        } else if (article.attr("href")
                                .contains(newsPaper.getSource()) && !article.attr("href")
                                .equals(section)) {
                            articlesList.add(article.attr("href"));
                        }
                    }
                }
                return articlesList;
            } else if (patterns.stream().findFirst().isPresent()) {
                Elements articleList = website_sec.select(patterns.stream().findFirst()
                        .get().getLoc_per_article());
                for (Element article : articleList) {
                    if (!(article.attr("href").startsWith(newsPaper.getSource())) &&
                            !(article.attr("href").startsWith("http://")) &&
                            !(article.attr("href").startsWith("https://")) &&
                            (article.attr("href").startsWith("/"))) {
                        articlesList.add(reCheckSource(newsPaper.getSource())
                                .concat(article.attr("href")));
                    } else if (article.attr("href").startsWith(newsPaper.getSource()) &&
                            !article.attr("href").equals(section) &&
                            !article.attr("href").equals(newsPaper.getSource())) {
                        articlesList.add(article.attr("href"));
                    }
                }
                return articlesList;
            }
        }
        articlesList.add("The pattern(s) didn't extract anything.");
        return articlesList;
    }

    @Override
    public List<String> tryArticleTitle(NewsPaper newsPaper, List<Pattern> patterns,
                                        Boolean useProxyy)
            throws IOException {
        List<String> sources = tryArticlesSource(newsPaper, newsPaper.getPatternList().stream()
                .filter(pattern -> !pattern.getLoc_section().isBlank() &&
                        !pattern.getLoc_per_article().isBlank() &&
                        !pattern.getLoc_art_title().isBlank()).toList(), useProxyy).stream()
                .filter(each -> !each.isBlank()).toList();
        List<String> titleList = new ArrayList<>();

        if (sources.isEmpty() || sources.stream().findFirst().get()
                .equals("The pattern(s) didn't extract anything.")) {
            titleList.add("The sections of this newspaper is empty " +
                    "and It's not possible continue the test.");
            return titleList;
        }
        for (String article : sources.size() > 10 ? sources.subList(0, 10) : sources) {
            Document website = obtainResponse(reCheckSource(article),
                    newsPaper.getProxyyList(), useProxyy).parse();
            if (patterns.size() > 1) {
                for (Pattern pattern : patterns) {
                    Element title = website
                            .select(pattern.getLoc_art_title()).first();
                    titleList.add(title != null ? title.text() : "");
                }
            } else if (patterns.stream().findFirst().isPresent()) {
                Element title = website.select(patterns.stream().findFirst().get()
                        .getLoc_art_title()).first();
                titleList.add(title != null ? title.text() : "");
            }
        }
        if (titleList.isEmpty()) {
            titleList.add("The pattern(s) didn't extract anything.");
        }
        return titleList;
    }

    @Override
    public List<String> tryArticleAuthors(NewsPaper newsPaper, List<Pattern> patterns,
                                          Boolean useProxyy)
            throws IOException {
        List<String> sources = tryArticlesSource(newsPaper, newsPaper.getPatternList(), useProxyy);
        List<String> authorsList = new ArrayList<>();

        if (sources.isEmpty() || sources.stream().findFirst().get()
                .equals("The pattern(s) didn't extract anything.")) {
            authorsList.add("The sections of this newspaper is empty " +
                    "and It's not possible continue the test.");
            return authorsList;
        }

        for (String article : sources.size() > 10 ? sources.subList(0, 10) : sources) {
            Document website = obtainResponse(reCheckSource(article),
                    newsPaper.getProxyyList(), useProxyy).parse();
            if (patterns.size() > 1) {
                for (Pattern pattern : patterns) {
                    Element authors = website
                            .select(pattern.getLoc_art_authors()).first();
                    authorsList.add(authors != null ? authors.text() : "");
                }
            } else if (patterns.stream().findFirst().isPresent()) {
                Element authors = website.select(patterns.stream().findFirst().get()
                        .getLoc_art_authors()).first();
                authorsList.add(authors != null ? authors.text() : "");
            }
        }
        if (authorsList.isEmpty()) {
            authorsList.add("The pattern(s) didn't extract anything.");
        }
        return authorsList;
    }

    @Override
    public List<String> tryArticleContent(NewsPaper newsPaper, List<Pattern> patterns, Boolean useProxyy)
            throws IOException {
        List<String> sources = tryArticlesSource(newsPaper, newsPaper.getPatternList(), useProxyy);
        List<String> contentList = new ArrayList<>();

        if (sources.isEmpty() || sources.stream().findFirst().get()
                .equals("The pattern(s) didn't extract anything.")) {
            contentList.add("The sections of this newspaper is empty " +
                    "and It's not possible continue the test.");
            return contentList;
        }

        for (String article : sources.size() > 10 ? sources.subList(0, 10) : sources) {
            Document website = obtainResponse(reCheckSource(article),
                    newsPaper.getProxyyList(), useProxyy).parse();
            if (patterns.size() > 1) {
                for (Pattern pattern : patterns) {
                    Elements content = website.select(pattern.getLoc_art_content());
                    contentList.add(!content.text().isBlank() ? content.text() : "");
                }
            } else if (patterns.stream().findFirst().isPresent()) {
                Elements content = website.select(patterns.stream().findFirst().get()
                        .getLoc_art_content());
                contentList.add(!content.text().isBlank() ? content.text() : "");
            }
        }
        if (contentList.isEmpty()) {
            contentList.add("The pattern(s) didn't extract anything.");
        }
        return contentList;
    }
}
