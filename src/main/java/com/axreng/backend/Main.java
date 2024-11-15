package com.axreng.backend;

import static spark.Spark.*;

import java.net.URI;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.dtos.*;
import com.axreng.backend.utils.GsonUtils;

public class Main {
    private static final CrawlerService crawlerService = new CrawlerService();
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        URI baseUrl = ConfigurationManager.createBaseUrl();
        port(4567);

        // Endpoint to start a crawl
        post("/crawl", (req, res) -> {
            res.type("application/json");

            try {
                SearchRequestDTO searchRequest = GsonUtils.fromJson(req.body(), SearchRequestDTO.class);

                if (searchRequest == null || !searchRequest.isValidKeyword()) {
                    res.status(HttpStatus.BAD_REQUEST_400);
                    return GsonUtils.toJson(new ErrorResponseDTO("Invalid request body or keyword"));
                }

                String searchId = crawlerService.startSearch(searchRequest.getKeyword(), baseUrl);
                return GsonUtils.toJson(new SearchResponseDTO(searchId));

            } catch (Exception e) {
                logger.error("Error processing /crawl request", e);
                res.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                return GsonUtils.toJson(new ErrorResponseDTO("Internal server error"));
            }
        });

        // Endpoint to get the crawl result by ID
        get("/crawl/:id", (req, res) -> {
            res.type("application/json");
            String searchId = req.params(":id");

            SearchResultDTO result = crawlerService.getSearchResult(searchId);
            if (result == null) {
                res.status(HttpStatus.NOT_FOUND_404);
                return GsonUtils.toJson(new ErrorResponseDTO("Search not found"));
            }

            return GsonUtils.toJson(result);
        });
        after((req, res) -> res.type("application/json"));
    }
}
