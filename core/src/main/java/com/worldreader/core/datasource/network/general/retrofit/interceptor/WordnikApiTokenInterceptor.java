package com.worldreader.core.datasource.network.general.retrofit.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;

public class WordnikApiTokenInterceptor implements Interceptor {

  private static final String API_KEY_HEADER = "X-Mashape-Key";
  private static final String JSON_HEADER_VALUE = "application/json";

  private String apiValue;

  public WordnikApiTokenInterceptor(String apiValue) {
    this.apiValue = apiValue;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Request.Builder builder = request.newBuilder();
    builder.header(API_KEY_HEADER, apiValue);
    builder.header("Accept", JSON_HEADER_VALUE);
    return chain.proceed(builder.build());
  }
}
