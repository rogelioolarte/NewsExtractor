package com.NewsExtractor.service;

import com.NewsExtractor.entity.NewsPaper;

import java.io.IOException;

public interface IManagementService {
    String extractAndBuild(NewsPaper newsPaper, Boolean useProxyy) throws IOException;
}
