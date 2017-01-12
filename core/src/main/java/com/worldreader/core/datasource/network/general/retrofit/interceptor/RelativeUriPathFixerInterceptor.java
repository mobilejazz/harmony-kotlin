package com.worldreader.core.datasource.network.general.retrofit.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;

public class RelativeUriPathFixerInterceptor implements Interceptor {

  private final String endpoint;

  public RelativeUriPathFixerInterceptor(String endpoint) {
    this.endpoint = endpoint;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    final Request request = chain.request();
    final String url = request.url().toString();

    if (url != null && url.startsWith("/")) {
      Request.Builder builder = request.newBuilder();
      builder.url(endpoint.concat(url));
      return chain.proceed(builder.build());
    }

    return chain.proceed(chain.request());
  }

}
