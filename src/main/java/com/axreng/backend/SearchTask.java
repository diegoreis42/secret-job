  package com.axreng.backend;

  import java.net.URI;
  import java.net.URISyntaxException;
  import java.net.http.HttpClient;
  import java.net.http.HttpRequest;
  import java.net.http.HttpResponse;
  import java.util.HashSet;
  import java.util.Map;
  import java.util.Set;
  import java.util.concurrent.CompletableFuture;
  import java.util.concurrent.CompletionStage;
  import java.util.regex.Matcher;
  import java.util.regex.Pattern;

  import com.axreng.backend.dtos.SearchResultDTO;
  import com.axreng.backend.enums.SearchStatus;

  public class SearchTask implements Runnable {
    private final String id;
    private final String keyword;
    private final URI baseUrl;
    private final Map<String, SearchResultDTO> searches;
    private final HttpClient httpClient;

    public SearchTask(String id, String keyword, URI baseUrl, Map<String, SearchResultDTO> searches) {
      this.id = id;
      this.keyword = keyword.toLowerCase();
      this.baseUrl = baseUrl;
      this.searches = searches;
      this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public void run() {
      try {
        crawlAsync(baseUrl, new HashSet<>()).thenRun(() -> {
          var searhResult = searches.get(id);

          if (searhResult != null)
            searhResult.setStatus(SearchStatus.DONE);
        }).join();

        var searhResult = searches.get(id);

        if (searhResult != null)
          searhResult.setStatus(SearchStatus.DONE);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private CompletableFuture<Void> crawlAsync(URI url, Set<URI> visited) {
      if (visited.contains(url)) {
        return CompletableFuture.completedFuture(null);
      }

      visited.add(url);

      SearchResultDTO result = searches.get(id);
      HttpRequest request = HttpRequest.newBuilder()
          .uri(url)
          .GET()
          .build();

      return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenCompose(response -> {

            String body = response.body();
            if (body.toLowerCase().contains(keyword)) {
              result.addUrl(url);
            }

            return extractAndFollowLinksAsync(body, url, visited);
          })
          .exceptionally(ex -> {
            System.err.println("Error fetching URL: " + url + " - " + ex.getMessage());
            return null;
          });

    }

    private CompletionStage<Void> extractAndFollowLinksAsync(String input, URI currentUrl, Set<URI> visited) {
      Pattern pattern = Pattern.compile("href=\"(.*?)\"", Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(input);

      Set<CompletableFuture<Void>> futures = new HashSet<>();

      while (matcher.find()) {
        String link = matcher.group(1);
        if (!link.startsWith("http")) {
          try {
            link = currentUrl.resolve(link).toString();
          } catch (Exception e) {
            System.err.println("Error resolving link: " + link);
            continue;
          }
        }

        if (link.startsWith(baseUrl.toString())) {
          try {
            futures.add(crawlAsync(new URI(link), visited));
          } catch (URISyntaxException e) {
            e.printStackTrace();
          }
        }
      }
      return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
  }