package com.axreng.backend.enums;

public enum SearchStatus {
  ACTIVE("active"),
  DONE("done");

  private final String status;

  SearchStatus(String string) {
    this.status = string;
  }

  public String getStatus() {
    return status;
  }
}
