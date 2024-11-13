package com.axreng.backend.dtos;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.axreng.backend.enums.SearchStatus;

public class SearchResultDTO {
  private final String id;
  private final String keyword;
  private String status;
  private final List<URL> urls;

  public SearchResultDTO(String id, String keyword) {
    this.id = id;
    this.keyword = keyword;
    this.status = SearchStatus.ACTIVE.getStatus();
    this.urls = new ArrayList<>();
  }

  public void addUrl(URL url) {
    urls.add(url);
  }

  public void setStatus(SearchStatus status) {
    this.status = status.getStatus();
  }
}
