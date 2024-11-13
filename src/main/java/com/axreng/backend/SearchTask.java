package com.axreng.backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.axreng.backend.dtos.SearchResultDTO;
import com.axreng.backend.enums.SearchStatus;

public class SearchTask implements Runnable {
  private final String id;
  private final String keyword;
  private final URL baseUrl;
  private final Map<String, SearchResultDTO> searches;

  public SearchTask(String id, String keyword, URL baseUrl, Map<String, SearchResultDTO> searches) {
    this.id = id;
    this.keyword = keyword.toLowerCase();
    this.baseUrl = baseUrl;
    this.searches = searches;
  }

  @Override
  public void run() {
    try {
      crawl(baseUrl);

      var searhResult = searches.get(id);

      if (searhResult != null)
        searhResult.setStatus(SearchStatus.DONE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void crawl(URL url) throws Exception {
    SearchResultDTO result = searches.get(id);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");

    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        if (inputLine.toLowerCase().contains(keyword)) {
          result.addUrl(url);
          break;
        }
      }
    }
  }
}
