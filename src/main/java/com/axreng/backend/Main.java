package com.axreng.backend;

import static spark.Spark.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.axreng.backend.dtos.ErrorResponseDTO;
import com.axreng.backend.dtos.SearchRequestDTO;
import com.axreng.backend.dtos.SearchResponseDTO;
import com.axreng.backend.dtos.SearchResultDTO;

public class Main {
    private static final CrawlerService crawlerService = new CrawlerService();
    private static final Gson gson = new Gson();
    private static final String DEFAULT_BASE_URL = "http://hiring.axreng.com/";

    public static void main(String[] args) {

        URI baseUrl = createBaseUrl();

        port(4567);

        post("/crawl", (req, res) -> {
            res.type("application/json");

            try {
                SearchRequestDTO searchRequest = gson.fromJson(req.body(), SearchRequestDTO.class);

                if (searchRequest == null || searchRequest.keyword == null) {
                    res.status(HttpStatus.BAD_REQUEST_400);
                    return gson.toJson(new ErrorResponseDTO("Invalid request body"));
                }

                if (!searchRequest.isValidKeyword()) {
                    res.status(HttpStatus.BAD_REQUEST_400);
                    return gson.toJson(new ErrorResponseDTO("Invalid keyword"));
                }

                String searchId = crawlerService.startSearch(searchRequest.keyword, baseUrl);
                return gson.toJson(new SearchResponseDTO(searchId));

            } catch (Exception e) {

                res.status(HttpStatus.BAD_REQUEST_400);
                return gson.toJson(new ErrorResponseDTO("Invalid request"));
            }
        });

        get("/crawl/:id", (req, res) -> {
            String searchId = req.params(":id");
            SearchResultDTO result = crawlerService.getSearchResult(searchId);

            if (result == null) {
                res.status(HttpStatus.NOT_FOUND_404);
                return "Search not found";
            }

            res.type("application/json");
            return gson.toJson(result);
        });
    }

    private static URI createBaseUrl() throws IllegalArgumentException {
        try {
            String baseUrl = System.getenv("BASE_URL") == null ? DEFAULT_BASE_URL
                    : System.getenv("BASE_URL");

            // Ensure baseUrl starts with http:// or https://
            if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
                throw new IllegalArgumentException("BASE_URL must start with http:// or https://");
            }

            // Ensure baseUrl ends with a slash
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }

            return new URI(baseUrl);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("BASE_URL is not a valid URI", e);
        }
    }
}
