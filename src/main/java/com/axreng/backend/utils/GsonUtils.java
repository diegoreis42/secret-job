package com.axreng.backend.utils;

import com.google.gson.Gson;

public class GsonUtils {
  private static final Gson gson = new Gson();

  public static <T> T fromJson(String json, Class<T> clazz) {
    return gson.fromJson(json, clazz);
  }

  public static String toJson(Object obj) {
    return gson.toJson(obj);
  }
}
