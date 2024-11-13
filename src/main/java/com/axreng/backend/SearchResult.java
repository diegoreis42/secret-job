package com.axreng.backend;

import java.util.ArrayList;
import java.util.List;

import com.axreng.backend.enums.SearchState;

public class SearchResult {
  private final String id;
  private final String keyword;
  private String status;
  private final List<String> urls;

  public SearchResult(String id, String keyword) {
    this.id = id;
    this.keyword = keyword;
    this.status = SearchState.ACTIVE.getState();
    this.urls = new ArrayList<>();
  }

  public void addUrl(String url) {
    urls.add(url);
  }

  public void setStatus(SearchState status) {
    this.status = status.getState();
  }
}