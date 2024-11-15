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

public class CrawlerService {
  private final Map<String, SearchResultDTO> searches = new ConcurrentHashMap<>();
  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final Logger logger = LoggerFactory.getLogger(CrawlerService.class);

  /**
   * Starts a new search for the given keyword.
   */
  public String startSearch(String keyword, URI baseUrl) {
    String id = DigestUtils.generateHash(keyword + baseUrl.toString()).substring(0, 8);

    if (searches.containsKey(id)) {
      logger.info("Search already in progress for keyword: {}, baseUrl: {}", keyword, baseUrl);
      return id;
    }

    SearchResultDTO result = new SearchResultDTO(id, keyword);
    searches.put(id, result);

    executor.submit(new SearchTask(id, keyword, baseUrl.toString(), searches));
    return id;
  }

  /**
   * Gets the search result by ID.
   */
  public SearchResultDTO getSearchResult(String id) {
    return searches.get(id);
  }
}
