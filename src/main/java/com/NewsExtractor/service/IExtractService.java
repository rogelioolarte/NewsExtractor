package com.NewsExtractor.service;

import com.NewsExtractor.dto.StateDTO;
import com.NewsExtractor.entity.Article;
import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.entity.Pattern;
import com.NewsExtractor.entity.Section;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface IExtractService {
    String reCheckSource(String source);

    String getStrings(HashMap<String, StateDTO> status);

    String extractNewTitle(NewsPaper newsPaper, Boolean useProxyy)
            throws IOException;

    HashMap<String, StateDTO> extractSections(NewsPaper newsPaper, List<Pattern> patterns,
                                              Boolean useProxyy) throws IOException;

    HashMap<String, StateDTO> extractArticlesSource(NewsPaper newsPaper, List<Pattern> patterns,
                                                    Boolean useProxyy) throws IOException;

    HashMap<String, StateDTO> extractArticles(NewsPaper newsPaper, List<Pattern> patterns,
                                              Boolean useProxyy) throws IOException;

    Integer saveSection(Element section, NewsPaper newsPaper);

    Integer saveArticlesSource(Element article, NewsPaper newsPaper,
                               Section section);

    Integer saveArticlesTitle(Article article, Element title);

    Integer saveArticlesAuthor(Article article, Element author);

    Integer saveArticlesContent(Article article, Elements content);

    void reRecheckArticles(NewsPaper newsPaper);

    void reCheckIds(NewsPaper newsPaper);

    void setCounters(NewsPaper newsPaper);

    void setSectionsCounters(List<Section> sectionList);

    void setNewsProgress(NewsPaper newsPaper, String string);

}
