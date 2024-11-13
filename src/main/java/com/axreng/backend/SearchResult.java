package com.axreng.backend;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
  private final String id;
  private final String keyword;
  private String status;
  private final List<String> urls;

  public SearchResult(String id, String keyword) {
    this.id = id;
    this.keyword = keyword;
    this.status = "active";
    this.urls = new ArrayList<>();
  }

  public void addUrl(String url) {
    urls.add(url);
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
