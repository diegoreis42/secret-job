package com.axreng.backend.dtos;

public class ErrorResponseDTO {
  String error;
  
  public ErrorResponseDTO(String errorMessage) {
    this.error = errorMessage;
  }

}
