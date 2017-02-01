package com.worldreader.core.datasource.network.general.retrofit.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AcceptApplicationJsonInterceptor implements Interceptor {

  @Override public Response intercept(final Chain chain) throws IOException {
    final Request originalRequest = chain.request();
    final Request.Builder newRequestBuilder = originalRequest.newBuilder();
    newRequestBuilder.addHeader("Accept", "application/json");
    final Request withNewRequest = newRequestBuilder.build();
    return chain.proceed(withNewRequest);
  }
  
}
