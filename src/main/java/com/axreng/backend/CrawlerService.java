package com.axreng.backend;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.axreng.backend.dtos.SearchResultDTO;
import com.axreng.backend.utils.DigestUtils;

public class CrawlerService {
  private final Map<String, SearchResultDTO> searches = new ConcurrentHashMap<>();
  private final ExecutorService executor = Executors.newCachedThreadPool();

  public String startSearch(String keyword, URI baseUrl) {
    String id = this.generateId(keyword, baseUrl);
    searches.put(id, new SearchResultDTO(id, keyword));
    executor.submit(new SearchTask(id, keyword, baseUrl, searches));
    return id;
  }

  public SearchResultDTO getSearchResult(String id) {
    return searches.get(id);
  }

  private String generateId(String keyword, URI baseUrl) {
    return DigestUtils.generateHash(keyword + baseUrl.toString()).substring(0, 8);
  }
}
