package com.axreng.backend.dtos;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.axreng.backend.enums.SearchStatus;

public class SearchResultDTO {
  private final String id;
  private final String keyword;
  private String status;
  private final Set<String> urls;

  public SearchResultDTO(String id, String keyword) {
    this.id = id;
    this.keyword = keyword;
    this.status = SearchStatus.ACTIVE.getStatus();
    this.urls = new ConcurrentSkipListSet<>();
  }

  public SearchResultDTO(String id, String keyword, SearchStatus failed) {
    this.id = id;
    this.keyword = keyword;
    this.status = failed.getStatus();
    this.urls = new ConcurrentSkipListSet<>();
  }

  public void addUrl(String url) {
    urls.add(url);
  }

  public void setStatus(SearchStatus status) {
    this.status = status.getStatus();
  }

  public String getKeyword() {
    return this.keyword;
  }

  public String getStatus() {
    return this.status;
  }

  public Set<String> getUrls() {
    return this.urls;
  }
}
