package com.worldreader.core.datasource.network.general.retrofit.interceptor;

import android.text.TextUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;

@Deprecated public class DynamicUrlInterceptor implements Interceptor {

  public static final String RESOURCE_HEADER_FLAG = "resource";

  @Override public Response intercept(Chain chain) throws IOException {
    final Request request = chain.request();
    final String url = request.url().toString();
    final String resourceHeaderValue = request.header(RESOURCE_HEADER_FLAG);

    if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(resourceHeaderValue)) {
      Request.Builder builder = request.newBuilder();
      builder.removeHeader(RESOURCE_HEADER_FLAG);
      builder.url(url.concat(resourceHeaderValue).trim());

      Request newRequest = builder.build();

      return chain.proceed(newRequest);
    }

    return chain.proceed(request);
  }

}
