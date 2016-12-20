package com.worldreader.core.datasource.network.exceptions;

import android.support.annotation.NonNull;
import com.worldreader.core.common.deprecated.error.exception.ErrorException;

public class NetworkErrorException2 extends ErrorException {

  public enum ErrorType {
    HTTP,
    NETWORK,
    CONVERSION,
    UNEXPECTED
  }

  private final ErrorType errorType;
  private final Throwable throwable;

  public static NetworkErrorException2 of(@NonNull ErrorType errorType,
      @NonNull Throwable throwable) {
    return new NetworkErrorException2(errorType, throwable);
  }

  private NetworkErrorException2(@NonNull ErrorType errorType, @NonNull Throwable throwable) {
    super(throwable);
    this.errorType = errorType;
    this.throwable = throwable;
  }

  public ErrorType getErrorType() {
    return errorType;
  }

  public Throwable getThrowable() {
    return throwable;
  }
}
