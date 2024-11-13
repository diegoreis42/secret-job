package com.axreng.backend.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils {

  public static String generateHash(String payload) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
      StringBuilder hash = new StringBuilder();
      for (byte b : hashBytes) {
        hash.append(String.format("%02x", b));
      }
      return hash.toString().substring(0, 8); // use the first 8 characters
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error generating hash", e);
    }
  }
}
