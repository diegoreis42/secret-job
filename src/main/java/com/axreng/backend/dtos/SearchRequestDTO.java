package com.axreng.backend.dtos;

public class SearchRequestDTO {
  public static final int MIN_KEYWORD_LENGTH = 4;
  public static final int MAX_KEYWORD_LENGTH = 32;

  private String keyword;

  public SearchRequestDTO(String keyword) {
    this.keyword = keyword.trim();
  }

  /**
   * Checks if the keyword is valid. Considers the length of the keyword.
   */
  public boolean isValidKeyword() {
    return keyword != null && keyword.length() >= MIN_KEYWORD_LENGTH && keyword.length() <= MAX_KEYWORD_LENGTH;
  }

  /**
   * Gets the search keyword.
   * @return the search keyword
   */
  public String getKeyword() {
    return this.keyword;
  }
}