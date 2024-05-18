package com.NewsExtractor.service.Impl;

import com.NewsExtractor.dto.StateDTO;
import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.service.IExtractService;
import com.NewsExtractor.service.IManagementService;
import com.NewsExtractor.service.IProxyyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Service
public class ManagmentServiceImpl implements IManagementService {

    @Autowired
    private IExtractService extractService;

    @Autowired
    private IProxyyService proxyyService;

    @Override
    public String extractAndBuild(NewsPaper newsPaper, Boolean useProxyy)
            throws IOException {
        StringBuilder executionTrace = new StringBuilder();
        if (!newsPaper.getSource().isBlank() && !newsPaper.getPatternList().isEmpty()) {
            if(useProxyy){
                proxyyService.checkProxyList(newsPaper.getSource(), newsPaper.getProxyyList());
            }
            String newTitle = extractService.extractNewTitle(newsPaper, useProxyy);
            HashMap<String, StateDTO> sections = extractService.extractSections(newsPaper,
                    newsPaper.getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_section().isBlank())
                            .toList(), useProxyy);

            HashMap<String, StateDTO> articlesSources = extractService.extractArticlesSource(newsPaper,
                    newsPaper.getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_per_article().isBlank())
                            .toList(), useProxyy);

            HashMap<String, StateDTO> articles = extractService.extractArticles(newsPaper,
                    newsPaper.getPatternList().stream()
                            .filter(pattern -> !pattern.getLoc_art_title().isBlank() ||
                                    !pattern.getLoc_art_authors().isBlank() ||
                                    !pattern.getLoc_art_content().isBlank())
                            .toList(), useProxyy);

            executionTrace.append(newTitle);
            executionTrace.append(extractService.getStrings(sections));
            executionTrace.append(extractService.getStrings(articlesSources));
            executionTrace.append(extractService.getStrings(articles));
            extractService.setCounters(newsPaper);
            extractService.setSectionsCounters(newsPaper.getSectionList());
            extractService.reCheckIds(newsPaper);
            extractService.setNewsProgress(newsPaper, "Extraction Completed");
        }
        return executionTrace.toString();
    }
}
