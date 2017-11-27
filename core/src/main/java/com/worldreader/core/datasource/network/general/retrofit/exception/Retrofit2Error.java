package com.worldreader.core.datasource.network.general.retrofit.exception;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.*;
import java.lang.annotation.Annotation;

// As Retrofit2 doesn't have the concept of RetrofitError like the previous version, this class handle this gap as the whole codebase depends on
// ErrorAdapters<RetrofitError> to notify back to the ui.
//
// Once the initial migration has been done, it should be a good idea to remove this class also.
public class Retrofit2Error extends RuntimeException {

  public static Retrofit2Error httpError(okhttp3.Response response) {
    final Response<Object> retrofitResponse = Response.error(response.body(), response);
    return httpError(retrofitResponse);
  }

  public static Retrofit2Error httpError(Response response) {
    final String message = response.code() + " " + response.message();
    return new Retrofit2Error(message, response.raw().request().url().toString(), response, Kind.HTTP, null, null);
  }

  public static Retrofit2Error conversionError(String url, Response response, IOException e) {
    return new Retrofit2Error(e.getMessage(), url, response, Kind.CONVERSION, e, null);
  }

  public static Retrofit2Error networkError(IOException exception) {
    return new Retrofit2Error(exception.getMessage(), null, null, Kind.NETWORK, exception, null);
  }

  public static Retrofit2Error unexpectedError(Throwable exception) {
    return new Retrofit2Error(exception.getMessage(), null, null, Kind.UNEXPECTED, exception, null);
  }

  /** Identifies the event kind which triggered a {@link Retrofit2Error}. */
  public enum Kind {
    /** An {@link IOException} occurred while communicating to the server. */
    NETWORK,
    /** An exception was thrown while (de)serializing a body. */
    CONVERSION,
    /** A non-200 HTTP status code was received from the server. */
    HTTP,
    /**
     * An internal error occurred while attempting to execute a request. It is best practice to
     * re-throw this exception so your application crashes.
     */
    UNEXPECTED
  }

  private final String url;
  private final Response response;
  private final Kind kind;
  private final Retrofit retrofit;

  Retrofit2Error(String message, String url, Response response, Kind kind, Throwable exception, Retrofit retrofit) {
    super(message, exception);
    this.url = url;
    this.response = response;
    this.kind = kind;
    this.retrofit = retrofit;
  }

  /** The request URL which produced the error. */
  public String getUrl() {
    return url;
  }

  /** Response object containing status code, headers, body, etc. */
  public Response getResponse() {
    return response;
  }

  public <T> T getErrorResponseAs(Class<T> type) {
    if (response != null && response.errorBody() != null) {
      try {
        return getBodyAs(type);
      } catch (RuntimeException e) {
        return null;
      }
    } else {
      return null;
    }
  }

  /** The event kind which triggered this error. */
  public Kind getKind() {
    return kind;
  }

  /** The Retrofit this request was executed on */
  public Retrofit getRetrofit() {
    return retrofit;
  }

  /**
   * HTTP response body converted to specified {@code type}. {@code null} if there is no
   * response.
   *
   * @throws Retrofit2Error if unable to convert the body to the specified {@code type}.
   */
  private <T> T getBodyAs(Class<T> type) throws RuntimeException {
    if (response == null || response.errorBody() == null) {
      return null;
    }
    Converter<ResponseBody, T> converter = retrofit.responseBodyConverter(type, new Annotation[0]);
    try {
      return converter.convert(response.errorBody());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
