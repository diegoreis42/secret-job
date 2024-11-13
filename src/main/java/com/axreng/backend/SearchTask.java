package com.axreng.backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.axreng.backend.enums.SearchState;

public class SearchTask implements Runnable {
  private final String id;
  private final String keyword;
  private final Map<String, SearchResult> searches;

  public SearchTask(String id, String keyword, Map<String, SearchResult> searches) {
    this.id = id;
    this.keyword = keyword;
    this.searches = searches;
  }

  @Override
  public void run() {
    try {
      String baseUrl = System.getenv("BASE_URL");
      if (baseUrl == null)
        baseUrl = "http://hiring.axreng.com/";

      crawl(baseUrl);

      searches.get(id).setStatus(SearchState.DONE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void crawl(String url) throws Exception {
    SearchResult result = searches.get(id);
    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
    conn.setRequestMethod("GET");

    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        if (inputLine.toLowerCase().contains(keyword.toLowerCase())) {
          result.addUrl(url);
          break;
        }
      }
    }
  }
}
