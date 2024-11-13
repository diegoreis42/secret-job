package com.axreng.backend.dtos;

import org.junit.jupiter.api.Test;

public class SearchRequestDTOTest {

  @Test
  void isValidKeyword() {
    SearchRequestDTO searchRequestDTO = new SearchRequestDTO("security");
    assert(searchRequestDTO.isValidKeyword() == true);
  }

  @Test
  void isValidKeyword_InvalidForLengthLessThanMinimum() {
    SearchRequestDTO searchRequestDTO = new SearchRequestDTO(new String(new char[SearchRequestDTO.MIN_KEYWORD_LENGTH - 1]).replace('\0', 'a'));
    assert(searchRequestDTO.isValidKeyword() == false);
  }

  @Test
  void isValidKeyword_InvalidForLengthGreaterThanMaximum() {
    SearchRequestDTO searchRequestDTO = new SearchRequestDTO(new String(new char[SearchRequestDTO.MAX_KEYWORD_LENGTH + 1]).replace('\0', 'a'));
    assert(searchRequestDTO.isValidKeyword() == false);
  }
}
