package com.axreng.backend;

import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.axreng.backend.dtos.SearchResultDTO;

public class CrawlerService {
  private final Map<String, SearchResultDTO> searches = new ConcurrentHashMap<>();
  private final ExecutorService executor = Executors.newCachedThreadPool();

  public String startSearch(String keyword, URL baseUrl) {
    String id = UUID.randomUUID().toString().substring(0, 8);

    executor.submit(new SearchTask(id, keyword, searches));
    return id;
  }

  public SearchResultDTO getSearchResult(String id) {
    return searches.get(id);
  }
}
