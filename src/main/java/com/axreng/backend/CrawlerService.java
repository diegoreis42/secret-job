package com.axreng.backend;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.dtos.SearchResultDTO;
import com.axreng.backend.utils.DigestUtils;

/**
 * Handles search requests and results.
 */
public class CrawlerService {
  private final Map<String, SearchResultDTO> searches = new ConcurrentHashMap<>();
  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final Logger logger = LoggerFactory.getLogger(CrawlerService.class);

  public String startSearch(String keyword, URI baseUrl) {
    // Generate a unique ID based on the keyword and baseUrl
    String id = DigestUtils.generateHash(keyword + baseUrl.toString()).substring(0, 8);

    if (searches.containsKey(id)) {
      logger.info("Search already in progress for keyword: {}, baseUrl: {}", keyword, baseUrl);
      return id;
    }

    // Create a new SearchResultDTO and start a search task
    SearchResultDTO result = new SearchResultDTO(id, keyword);
    searches.put(id, result);

    executor.submit(new SearchTask(id, keyword, baseUrl.toString(), searches));
    return id;
  }

  public SearchResultDTO getSearchResult(String id) {
    return searches.get(id);
  }
}