package com.worldreader.core.datasource.network.general.retrofit.interceptor;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.domain.model.OAuthResponse;
import com.worldreader.core.domain.repository.OAuthRepository;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import javax.inject.Inject;
import java.io.*;
import java.util.regex.*;

public class WorldreaderOAuthTokenInterceptor2 implements Interceptor {

  private static final String AUTHORIZATION = "Authorization";
  private static final String BEARER = "Bearer ";

  private static final Pattern TOKEN_REQUEST_PATTERN = Pattern.compile("/token$");

  private static final Pattern APPLICATION_TOKEN_REQUEST_PATTERN =
      Pattern.compile("/(?:register|reset-password)$");

  private final Logger logger;

  private OAuthRepository repository;

  private enum RequestType {
    TOKEN_REQUEST, NORMAL_REQUEST,
  }

  @Inject public WorldreaderOAuthTokenInterceptor2(final Logger logger) {
    this.logger = logger;
  }

  @Override public Response intercept(final Chain chain) throws IOException {
    final Request originalRequest = chain.request();
    final HttpUrl url = originalRequest.url();

    final RequestType requestType = getRequestTypeFor(url);

    switch (requestType) {
      case TOKEN_REQUEST:
        return handleClientCredentialsTokenRequest(chain, originalRequest, url);
      case NORMAL_REQUEST:
        return handleNormalRequest(chain, originalRequest, url);
      default:
        throw new IllegalArgumentException(
            "requestType does not have an associated action for this url!");
    }
  }

  public void setOAuthRepository(final OAuthRepository repository) {
    this.repository = repository;
  }

  private RequestType getRequestTypeFor(final HttpUrl url) {
    final String path = url.encodedPath();
    if (TOKEN_REQUEST_PATTERN.matcher(path).find()) {
      return RequestType.TOKEN_REQUEST;
    } else {
      return RequestType.NORMAL_REQUEST;
    }
  }

  private Response handleClientCredentialsTokenRequest(final Chain chain,
      final Request originalRequest, final HttpUrl url) throws IOException {
    return chain.proceed(
        originalRequest); // Allow the request to proceed properly, we want client_credentials token
  }

  private Response handleNormalRequest(final Chain chain, final Request originalRequest,
      final HttpUrl url) throws IOException {
    final String path = url.encodedPath();
    final Matcher matcher = APPLICATION_TOKEN_REQUEST_PATTERN.matcher(path);
    final boolean applicationTokenRequest = matcher.find();

    Request.Builder newRequestBuilder = originalRequest.newBuilder();
    String token = "";
    if (applicationTokenRequest) {
      final OAuthResponse oAuthResponse = repository.applicationToken(); // TODO: 12/07/2017 Review when an IllegalTokenException is thrown, esponse get hanged
      if (oAuthResponse != null) {
        token = oAuthResponse.getAccessToken();
      }
    } else {
      final OAuthResponse oAuthResponse = repository.userToken();
      if (oAuthResponse != null) {
        token = oAuthResponse.getAccessToken();
      }
    }

    newRequestBuilder.addHeader(AUTHORIZATION, BEARER + token);

    final Request newRequest = newRequestBuilder.build();

    return chain.proceed(newRequest);
  }

}
