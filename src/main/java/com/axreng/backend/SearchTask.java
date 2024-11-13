package com.axreng.backend;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.axreng.backend.dtos.SearchResultDTO;
import com.axreng.backend.enums.SearchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles search requests and results.
 */
public class SearchTask implements Runnable {
  private final String id;
  private final String keyword;
  private final String baseUrl;
  private final Map<String, SearchResultDTO> searches;
  private final HttpClient httpClient;
  private final Pattern pattern = Pattern.compile("href=\"(.*?)\"", Pattern.CASE_INSENSITIVE);
  private final Logger logger = LoggerFactory.getLogger(SearchTask.class);

  public SearchTask(String id, String keyword, String baseUrl, Map<String, SearchResultDTO> searches) {
    this.id = id;
    this.keyword = keyword.toLowerCase();
    this.searches = searches;
    this.baseUrl = baseUrl;
    this.httpClient = HttpClient.newHttpClient(); // Use a single, reusable HttpClient instance
  }

  /**
   * Starts the search task.
   */
  @Override
  public void run() {
    try {
      // Use ConcurrentHashMap's newKeySet() to avoid ConcurrentModificationException
      Set<String> visited = ConcurrentHashMap.newKeySet();
      crawlAsync(baseUrl, visited)
          .thenRun(() -> searches.get(id).setStatus(SearchStatus.DONE))
          .join(); // Wait for all async tasks to complete
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Crawls the input URL asynchronously.
   */
  private CompletableFuture<Void> crawlAsync(String url, Set<String> visited) {
    // Thread-safe check-and-add
    if (!visited.add(url)) {
      return CompletableFuture.completedFuture(null);
    }

    SearchResultDTO result = searches.get(id);
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .GET()
        .build();

    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
        .thenCompose(response -> {
          try (InputStream bodyStream = response.body()) {
            StringBuilder bodyBuilder = new StringBuilder();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bodyStream.read(buffer)) != -1) {
              bodyBuilder.append(new String(buffer, 0, bytesRead));
              if (bodyBuilder.toString().toLowerCase().contains(this.keyword)) {
                synchronized (result) {
                  result.addUrl(url); // Synchronize updates to SearchResult
                }
              }
            }
            String body = bodyBuilder.toString();
            return extractAndFollowLinksAsync(body, url, visited);
          } catch (IOException e) {
            logger.error("Error reading response body: " + url + " - " + e.getMessage());
            return CompletableFuture.completedFuture(null);
          }
        })
        .exceptionally(ex -> {
          logger.error("Error fetching URL: " + url + " - " + ex.getMessage());
          return null;
        });
  }

  /**
   * Extracts and follows links from the input HTML asynchronously.
   */
  private CompletableFuture<Void> extractAndFollowLinksAsync(String input, String currentUrl, Set<String> visited) {
    Matcher matcher = this.pattern.matcher(input);

    Set<CompletableFuture<Void>> futures = ConcurrentHashMap.newKeySet();

    while (matcher.find()) {
      String link = matcher.group(1);
      if (!link.startsWith("http")) {
        try {
          link = new URI(currentUrl).resolve(link).toString();
        } catch (Exception e) {
          logger.error("Error resolving link: " + link);
          continue;
        }
      }

      if (link.startsWith(baseUrl)) {
        futures.add(crawlAsync(link, visited));
      }
    }

    // Wait for all asynchronous link crawls to complete
    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
  }
}