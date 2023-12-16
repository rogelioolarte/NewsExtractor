package com.NewsExtractor.service;

import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.entity.Pattern;

import java.io.IOException;
import java.util.List;

public interface ITestService {

    String sourceName(String source);

    String reCheckSource(String source);

    String tryNewTitle(NewsPaper newsPaper, Boolean useProxyy) throws IOException;

    List<String> trySections(NewsPaper newsPaper, List<Pattern> patterns, Boolean useProxyy)
            throws IOException;

    List<String> tryArticlesSource(NewsPaper newsPaper, List<Pattern> patterns, Boolean useProxyy)
            throws IOException;

    List<String> tryArticleTitle(NewsPaper newsPaper, List<Pattern> patterns, Boolean useProxyy)
            throws IOException;

    List<String> tryArticleAuthors(NewsPaper newsPaper, List<Pattern> patterns, Boolean useProxyy)
            throws IOException;

    List<String> tryArticleContent(NewsPaper newsPaper, List<Pattern> patterns, Boolean useProxyy)
            throws IOException;
}
