package com.NewsExtractor.service.Impl;

import com.NewsExtractor.dto.StateDTO;
import com.NewsExtractor.entity.*;
import com.NewsExtractor.service.*;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;

@Service
public class ExtractServiceImpl implements IExtractService {

    private final String generalAgentToUse = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 " +
            "Safari/537.36";
    private final Connection session = Jsoup.newSession().timeout(2000)
            .userAgent(generalAgentToUse).ignoreHttpErrors(true);

    @Autowired
    private INewsPaperService newsPaperService;
    @Autowired
    private ISectionService sectionService;
    @Autowired
    private IArticleService articleService;
    @Autowired
    private IProxyyService proxyyService;

    @Override
    public String reCheckSource(String source) {
        return source.endsWith("/") ? source.substring(0, source.lastIndexOf("/")) :
                source;
    }

    @Override
    public String getStrings(HashMap<String, StateDTO> status) {
        if (status != null && !status.isEmpty()) {
            if (status.containsKey("Sections")) {
                return status.get("Sections").getErrors().toString() +
                        " Sections have not been extracted due to a connection error \n" +
                        status.get("Sections").getIgnored().toString() +
                        " Sections have not been saved because they already exist \n";
            } else if (status.containsKey("Article sources")) {
                return status.get("Article sources").getErrors().toString() +
                        " Article sources have not been extracted due to a connection error \n" +
                        status.get("Article sources").getIgnored().toString() +
                        " Article sources have not been saved because they already exist \n";
            } else if (status.containsKey("Titles")) {
                return status.get("Titles").getErrors().toString() +
                        " Titles have not been extracted due to a connection error \n" +
                        status.get("Titles").getIgnored().toString() +
                        " Titles have not been saved because they already exist \n";
            } else if (status.containsKey("Authors")) {
                return status.get("Authors").getErrors().toString() +
                        " Authors have not been extracted due to a connection error \n" +
                        status.get("Authors").getIgnored().toString() +
                        " Authors have not been saved because they already exist \n";
            } else if (status.containsKey("Contents")) {
                return status.get("Contents").getErrors().toString() +
                        " Contents have not been extracted due to a connection error \n" +
                        status.get("Contents").getIgnored().toString() +
                        " Contents have not been saved because they already exist \n";
            }
        }
        return "Error while processing...";
    }

    public Connection.Response obtainResponse(String source, List<Proxyy> proxyyList,
                                              Boolean useProxyy) throws IOException {
        try {
            if (useProxyy && !proxyyList.isEmpty()) {
                return session.newRequest(source)
                        .proxy(proxyyService.returnOne(proxyyService.usefulProxy(proxyyList))).execute();
            }

            return session.newRequest(source).execute();
        }
        catch (SocketTimeoutException | MalformedURLException |
                HttpStatusException | UnsupportedMimeTypeException e){
            // This will return a 404 error until a new update
            return session.newRequest("https://reqres.in/api/unknown/23")
                    .ignoreContentType(true).execute();
        }
    }

    @Override
    public String extractNewTitle(NewsPaper newsPaper, Boolean useProxyy)
            throws IOException {
        Connection.Response response = obtainResponse(newsPaper.getSource(),
                newsPaper.getProxyyList(), useProxyy);
        if (response.statusCode() >= 400) {
            return "Error connecting while extracting the new title... \n";
        }
        Document website = response.parse();
        if (website.title().length() < 40) {
            newsPaper.setName(website.title());
            newsPaperService.save(newsPaper);
            return "The title has been changed to " + website.title() + "\n";
        }
        return "The title has not been changed" + "\n";
    }

    @Override
    public HashMap<String, StateDTO> extractSections(NewsPaper newsPaper, List<Pattern> patterns,
                                                     Boolean useProxyy) throws IOException {
        HashMap<String, StateDTO> stateDTOList = new HashMap<>();
        StateDTO stateDTO = StateDTO.builder().errors(0).ignored(0).build();
        Connection.Response response = obtainResponse(newsPaper.getSource(),
                newsPaper.getProxyyList(), useProxyy);
        if (response.statusCode() >= 400) {
            stateDTO.setErrors(stateDTO.getErrors() + 1);
        }
        Document website = response.parse();
        if (patterns.size() > 1) {
            for (Pattern pattern : patterns) {
                Elements sectionList = website.select(pattern.getLoc_section());
                for (Element section : sectionList) {
                    stateDTO.setIgnored(stateDTO.getIgnored() + saveSection(section, newsPaper));
                }
            }
        } else if (patterns.stream().findFirst().isPresent()) {
            Elements sectionList = website.select(patterns.stream()
                    .findFirst().get().getLoc_section());
            for (Element section : sectionList) {
                stateDTO.setIgnored(stateDTO.getIgnored() + saveSection(section, newsPaper));
            }
        }
        stateDTOList.put("Sections", stateDTO);
        return stateDTOList;
    }

    @Override
    public HashMap<String, StateDTO> extractArticlesSource(NewsPaper newsPaper, List<Pattern> patterns,
                                                           Boolean useProxyy) throws IOException {
        HashMap<String, StateDTO> stateDTOList = new HashMap<>();
        StateDTO stateDTO = StateDTO.builder().errors(0).ignored(0).build();
        for (Section section : newsPaper.getSectionList()) {
            Connection.Response response = obtainResponse(section.getSource(),
                    newsPaper.getProxyyList(), useProxyy);
            if (response.statusCode() >= 400) {
                stateDTO.setErrors(stateDTO.getErrors() + 1);
                continue;
            }
            Document website = response.parse();
            if (patterns.size() > 1) {
                for (Pattern pattern : patterns) {
                    Elements articleList = website.select(pattern.getLoc_per_article());
                    for (Element article : articleList) {
                        stateDTO.setIgnored(stateDTO.getIgnored() +
                                saveArticlesSource(article, newsPaper, section));
                    }
                }
            } else if (patterns.stream().findFirst().isPresent()) {
                Elements articleList = website.select(patterns.stream().findFirst()
                        .get().getLoc_per_article());
                for (Element article : articleList) {
                    stateDTO.setIgnored(stateDTO.getIgnored() +
                            saveArticlesSource(article, newsPaper, section));
                }
            }
        }
        stateDTOList.put("Article sources", stateDTO);
        return stateDTOList;
    }

    @Override
    public HashMap<String, StateDTO> extractArticles(NewsPaper newsPaper,
                                                     List<Pattern> patterns, Boolean useProxyy)
            throws IOException {
        HashMap<String, StateDTO> stateDTOList = new HashMap<>();
        StateDTO stateTitles = StateDTO.builder().errors(0).ignored(0).build();
        StateDTO stateAuthors = StateDTO.builder().errors(0).ignored(0).build();
        StateDTO stateContents = StateDTO.builder().errors(0).ignored(0).build();
        for (Article article : newsPaper.getArticleList()) {
            if (article.getTitle().isBlank() || article.getAuthors().isBlank()
                    || article.getContent().isBlank()) {
                Connection.Response request = obtainResponse(article.getSource(),
                        newsPaper.getProxyyList(), useProxyy);
                if (request.statusCode() >= 400) {
                    stateTitles.setErrors(stateTitles.getErrors() + 1);
                    stateAuthors.setErrors(stateAuthors.getErrors() + 1);
                    stateContents.setErrors(stateContents.getErrors() + 1);
                    continue;
                }
                Document website = request.parse();
                if (patterns.size() > 1) {
                    for (Pattern pattern : patterns) {
                        Element title = website.select(pattern.getLoc_art_title()).first();
                        Element author = website.select(pattern.getLoc_art_authors()).first();
                        Elements content = website.select(pattern.getLoc_art_content());
                        stateTitles.setIgnored(stateTitles.getIgnored() +
                                saveArticlesTitle(article, title));
                        stateAuthors.setIgnored(stateAuthors.getIgnored() +
                                saveArticlesAuthor(article, author));
                        stateContents.setIgnored(stateContents.getIgnored() +
                                saveArticlesContent(article, content));
                    }
                } else if (patterns.stream().findFirst().isPresent()) {
                    Element title = website.select(patterns.stream().findFirst().get()
                            .getLoc_art_title()).first();
                    Element author = website.select(patterns.stream().findFirst().get()
                            .getLoc_art_authors()).first();
                    Elements content = website.select(patterns.stream().findFirst().get()
                            .getLoc_art_content());
                    stateTitles.setIgnored(stateTitles.getIgnored() +
                            saveArticlesTitle(article, title));
                    stateAuthors.setIgnored(stateAuthors.getIgnored() +
                            saveArticlesAuthor(article, author));
                    stateContents.setIgnored(stateContents.getIgnored() +
                            saveArticlesContent(article, content));
                }
            }
        }
        stateDTOList.put("Titles", stateTitles);
        stateDTOList.put("Authors", stateTitles);
        stateDTOList.put("Contents", stateTitles);
        return stateDTOList;
    }

    @Override
    public Integer saveSection(Element section, NewsPaper newsPaper) {
        int number = 0;
        if (!sectionService.existsBySource(!(section.attr("href")
                .contains(newsPaper.getSource())) ?
                reCheckSource(newsPaper.getSource()).concat(section.attr("href")) :
                section.attr("href"))) {
            if (!(section.attr("href")
                    .contains(newsPaper.getSource())) && !(section.attr("href")
                    .startsWith("http://")) &&
                    !(section.attr("href").startsWith("https://")) &&
                    (section.attr("href").startsWith("/"))) {
                sectionService.save(Section.builder()
                        .source(reCheckSource(newsPaper.getSource()).
                                concat(section.attr("href")))
                        .total_articles(0).newsPaper(newsPaper).build());
                number += 0;
            } else if (section.attr("href").startsWith(newsPaper.getSource())) {
                sectionService.save(Section.builder()
                        .source(section.attr("href"))
                        .total_articles(0).newsPaper(newsPaper).build());
                number += 0;
            }
        } else {
            number += 1;
        }
        return number;
    }

    @Override
    public Integer saveArticlesSource(Element article, NewsPaper newsPaper,
                                      Section section) {
        int number = 0;
        if (!articleService.existsBySource(
                !(article.attr("href").contains(newsPaper.getSource())) ?
                        reCheckSource(newsPaper.getSource())
                                .concat(article.attr("href")) :
                        article.attr("href"))) {
            if (!(article.attr("href").startsWith(newsPaper.getSource())) &&
                    !(article.attr("href").startsWith("http://")) &&
                    !(article.attr("href").startsWith("https://")) &&
                    (article.attr("href").startsWith("/"))) {
                articleService.save(Article.builder()
                        .source(reCheckSource(newsPaper.getSource())
                                .concat(article.attr("href")))
                        .title("").authors("").content("").section(section)
                        .newsPaper(newsPaper).build());
                number += 0;
            } else if (article.attr("href").startsWith(newsPaper.getSource()) &&
                    !article.attr("href").equals(section.getSource()) &&
                    !article.attr("href").equals(newsPaper.getSource())) {
                articleService.save(Article.builder()
                        .source(article.attr("href"))
                        .title("").authors("").content("").section(section)
                        .newsPaper(newsPaper).build());
                number += 0;
            }
        } else {
            number += 1;
        }
        return number;
    }

    @Override
    public Integer saveArticlesTitle(Article article, Element title) {
        if (title != null && !title.text().isBlank()) {
            article.setTitle(title.text());
            articleService.save(article);
            return 0;
        }
        return 1;
    }

    @Override
    public Integer saveArticlesAuthor(Article article, Element author) {
        if (author != null && !author.text().isBlank()) {
            article.setAuthors(author.text());
            articleService.save(article);
            return 0;
        }
        return 1;
    }

    @Override
    public Integer saveArticlesContent(Article article, Elements content) {
        if (content != null && !content.text().isBlank()) {
            article.setContent(content.text());
            articleService.save(article);
            return 0;
        }
        return 1;
    }

    @Override
    public void reRecheckArticles(NewsPaper newsPaper) {
        for (Article article : newsPaper.getArticleList()) {
            if (article.getSource().equals(article.getSection().getSource())) {
                articleService.deleteById(article.getId());
            }
        }
    }

    @Override
    public void reCheckIds(NewsPaper newsPaper) {
        long artId = 1;
        long secsId = 1;
        long patId = 1;
        long proId = 1;
        for(Article article : newsPaper.getArticleList()) {
            article.setId(artId++);
        }
        for(Section section : newsPaper.getSectionList()) {
            section.setId(secsId++);
        }
        for(Pattern pattern : newsPaper.getPatternList()) {
            pattern.setId(patId++);
        }
        for (Proxyy proxyy : newsPaper.getProxyyList()){
            proxyy.setId(proId++);
        }
    }

    @Override
    public void setCounters(NewsPaper newsPaper) {
        newsPaper.setTotal_sections(newsPaper.getSectionList().size());
        newsPaper.setTotal_articles(newsPaper.getArticleList().size());
        newsPaperService.save(newsPaper);
    }

    @Override
    public void setSectionsCounters(List<Section> sectionList) {
        for (Section section : sectionList) {
            section.setTotal_articles(section.getArticleList().size());
        }
    }

    @Override
    public void setNewsProgress(NewsPaper newsPaper, String string) {
        newsPaper.setProgress(string);
        newsPaperService.save(newsPaper);
    }
}