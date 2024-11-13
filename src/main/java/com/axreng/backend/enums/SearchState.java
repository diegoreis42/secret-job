package com.axreng.backend.enums;

public enum SearchState {
  ACTIVE("active"),
  DONE("done");

  private final String state;

  SearchState(String string) {
    this.state = string;
  }

  public String getState() {
    return state;
  }
}
