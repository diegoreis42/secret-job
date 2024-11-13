package com.axreng.backend;

import static spark.Spark.*;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.axreng.backend.dtos.ErrorResponseDTO;
import com.axreng.backend.dtos.SearchRequestDTO;
import com.axreng.backend.dtos.SearchResponseDTO;

public class Main {
    private static final CrawlerService crawlerService = new CrawlerService();
    private static final Gson gson = new Gson();
    private static final String DEFAULT_BASE_URL = "http://hiring.axreng.com/";

    public static void main(String[] args) {

        URL baseUrl = createBaseUrl();

        port(4567);

        post("/crawl", (req, res) -> {
            res.type("application/json");

            try {
                SearchRequestDTO searchRequest = gson.fromJson(req.body(), SearchRequestDTO.class);

                String searchId = crawlerService.startSearch(searchRequest.keyword, baseUrl);
                return gson.toJson(new SearchResponseDTO(searchId));

            } catch (Exception e) {

                res.status(400);
                return gson.toJson(new ErrorResponseDTO("Invalid request"));
            }
        });

        get("/crawl/:id", (req, res) -> "GET /crawl/" + req.params("id"));
    }

    private static URL createBaseUrl() throws IllegalArgumentException {
        try {
            String baseUrl = System.getenv("BASE_URL") == null ? DEFAULT_BASE_URL
                    : System.getenv("BASE_URL");

            // Default baseUrl don't need validation
            if (baseUrl.equals(DEFAULT_BASE_URL)) {
                return new URL(DEFAULT_BASE_URL);
            }

            // Ensure baseUrl starts with http:// or https://
            if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
                throw new IllegalArgumentException("BASE_URL must start with http:// or https://");
            }

            // Ensure baseUrl ends with a slash
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }

            return new URL(baseUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("BASE_URL is not a valid URL", e);
        }
    }
}
