package com.worldreader.core.datasource.network.general.retrofit.interceptor;

import android.support.annotation.NonNull;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.application.di.qualifiers.WorldreaderBookApiEndpointToken;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Implementation of the okHttp interceptor to comply with the requirements of the WorldReader
 * Server.
 */
public class WorldreaderBooksServerInterceptor implements Interceptor {

  public static final String TAG = "SERVER_WORLDREADER";

  private static final String WORLDREADER_ANDROID_CLIENT = "org.worldreader.wrms.android/1.0.0.0";

  private final String token;
  private final  Logger logger;

  @Inject public WorldreaderBooksServerInterceptor(final @WorldreaderBookApiEndpointToken String token, Logger logger) {
    this.token = token;
    this.logger = logger;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();

    //Build new request
    Request.Builder builder = request.newBuilder();

    try {
      String timestamp = String.valueOf(System.currentTimeMillis());
      String hash = buildRequestHash(request.url(), timestamp);

      builder.url(request.url()
          .toString()
          .replaceAll("\\s", "%20")
          .replaceAll("\\+", "%20")
          .replaceAll("!", "%21")
          .replaceAll("'", "%27")
          .replaceAll("\\(", "%28")
          .replaceAll("\\)", "%29")
          .replaceAll("~", "%7E"));

      builder.addHeader("X-Worldreader-Client", WORLDREADER_ANDROID_CLIENT);
      builder.addHeader("X-Worldreader-Timestamp", timestamp);
      builder.addHeader("X-Worldreader-Hash", hash);
      request = builder.build();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    long t1 = System.nanoTime();
    //logger.d(TAG, String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

    Response response = chain.proceed(request);

    long t2 = System.nanoTime();
    //logger.d(TAG, String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));

    return response;
  }

  /**
   * Generate the full request path
   *
   * @param httpUrl - {@link HttpUrl} Object to get all the parts of the request
   * @return Full path request
   */
  @NonNull private String buildPath(HttpUrl httpUrl) {
    String path = httpUrl.encodedPath();
    String query = httpUrl.query();

    StringBuilder encodedPathBuilder = new StringBuilder();
    encodedPathBuilder.append(path);

    if (query != null && query.length() > 0) {
      encodedPathBuilder.append("?");
      encodedPathBuilder.append(query);
    }

    // Custom fix for known characters that we know for sure that it should be properly encoded
    return encodedPathBuilder.toString()
        .replaceAll("\\s", "%20")
        .replaceAll("\\+", "%20")
        .replaceAll("!", "%21")
        .replaceAll("'", "%27")
        .replaceAll("\\(", "%28")
        .replaceAll("\\)", "%29")
        .replaceAll("~", "%7E");
  }

  /**
   * Generate the String that we need to generate the hash for the request header.
   * Example of value to generate: (path + timestamp + token)
   *
   * @param httpUrl - {@link HttpUrl} Object to get all the parts of the request
   * @param timestamp - It's a requirement of the API
   * @return Value to make the hash
   */
  @NonNull private String buildStringToHash(HttpUrl httpUrl, String timestamp) {
    String path = buildPath(httpUrl);

    StringBuilder builder = new StringBuilder();
    builder.append(path);
    builder.append(timestamp);
    builder.append(token);

    return builder.toString();
  }

  /**
   * Generate the hash that we need to put on the header for the request authentication.
   *
   * @param httpUrl - {@link HttpUrl} Object to get all the parts of the request
   * @param timestamp - It's a requirement of the API
   * @return Request hash
   * @throws NoSuchAlgorithmException
   */
  private String buildRequestHash(HttpUrl httpUrl, String timestamp) throws NoSuchAlgorithmException {
    String valueToHash = buildStringToHash(httpUrl, timestamp);
    return sha256(valueToHash);
  }

  private String sha256(String value) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(value.getBytes());

    byte byteData[] = md.digest();

    //convert the byte to hex format method 1
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
      sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }

    return sb.toString();
  }
}
