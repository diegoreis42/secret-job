package com.axreng.backend.dtos;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.axreng.backend.enums.SearchStatus;

public class SearchResultDTO {

  private final String id;
  private final String keyword;
  private volatile String status;
  private final Set<String> urls;

  public SearchResultDTO(String id, String keyword) {
    this.id = id;
    this.keyword = keyword;
    this.status = SearchStatus.ACTIVE.getStatus();
    this.urls = ConcurrentHashMap.newKeySet();
  }

  public SearchResultDTO(String id, String keyword, SearchStatus status) {
    this.id = id;
    this.keyword = keyword;
    this.status = status.getStatus();
    this.urls = ConcurrentHashMap.newKeySet();
  }

  /**
   * Adds a URL to the result set.
   *
   * @param url URL to add
   */
  public void addUrl(String url) {
    if (url != null && !url.trim().isEmpty()) {
      urls.add(url);
    }
  }

  /**
   * Sets the search status.
   *
   * @param status New search status
   */
  public void setStatus(SearchStatus status) {
    if (status != null) {
      this.status = status.getStatus();
    }
  }

  /**
   * Gets the search keyword.
   *
   * @return the search keyword
   */
  public String getKeyword() {
    return this.keyword;
  }

  /**
   * Gets the current search status.
   *
   * @return the search status
   */
  public String getStatus() {
    return this.status;
  }
}
