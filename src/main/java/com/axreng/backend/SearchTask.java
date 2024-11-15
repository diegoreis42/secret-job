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

public class SearchTask implements Runnable {
    private final String id;
    private final String keyword;
    private final String baseUrl;
    private final Map<String, SearchResultDTO> searches;
    private final HttpClient httpClient;
    private static final Pattern pattern = Pattern.compile("href=\"(.*?)\"", Pattern.CASE_INSENSITIVE);
    private static final Logger logger = LoggerFactory.getLogger(SearchTask.class);

    public SearchTask(String id, String keyword, String baseUrl, Map<String, SearchResultDTO> searches) {
        this.id = id;
        this.keyword = keyword.toLowerCase();
        this.searches = searches;
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public void run() {
        try {
            Set<String> visited = ConcurrentHashMap.newKeySet();
            crawlAsync(baseUrl, visited)
                    .thenRun(() -> searches.get(id).setStatus(SearchStatus.DONE))
                    .join();
        } catch (Exception e) {
            logger.error("Error in search task", e);
        }
    }

    private CompletableFuture<Void> crawlAsync(String url, Set<String> visited) {
        if (!visited.add(url))
            return CompletableFuture.completedFuture(null);

        SearchResultDTO result = searches.get(id);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenCompose(response -> handleResponse(response, url, visited, result))
                .exceptionally(ex -> {
                    logger.error("Error fetching URL: {}", url, ex);
                    return null;
                });
    }

    private CompletableFuture<Void> handleResponse(HttpResponse<InputStream> response, String url,
            Set<String> visited, SearchResultDTO result) {
        try (InputStream bodyStream = response.body()) {
            String body = new String(bodyStream.readAllBytes());
            if (body.toLowerCase().contains(keyword)) {
                synchronized (result) {
                    result.addUrl(url);
                }
            }
            return extractAndFollowLinksAsync(body, url, visited);
        } catch (IOException e) {
            logger.error("Error reading response body: {}", url, e);
            return CompletableFuture.completedFuture(null);
        }
    }

    private CompletableFuture<Void> extractAndFollowLinksAsync(String body, String currentUrl, Set<String> visited) {
        Matcher matcher = pattern.matcher(body);
        Set<CompletableFuture<Void>> futures = ConcurrentHashMap.newKeySet();

        while (matcher.find()) {
            String link = matcher.group(1);
            if (!link.startsWith("http")) {
                link = URI.create(currentUrl).resolve(link).toString();
            }
            if (link.startsWith(baseUrl)) {
                futures.add(crawlAsync(link, visited));
            }
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
