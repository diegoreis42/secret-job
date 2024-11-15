package com.axreng.backend;

import java.net.URI;
import java.net.URISyntaxException;

public class ConfigurationManager {
  private static final String DEFAULT_BASE_URL = "http://hiring.axreng.com/";

  public static URI createBaseUrl() {
    try {
      String baseUrl = System.getenv().getOrDefault("BASE_URL", DEFAULT_BASE_URL);
      if (!baseUrl.startsWith("http")) {
        throw new IllegalArgumentException("BASE_URL must start with http:// or https://");
      }
      return new URI(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/");
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Invalid BASE_URL", e);
    }
  }
}
