package com.worldreader.core.error.user;

import com.worldreader.core.datasource.exceptions.DataSourceException;

public class NetworkErrorException extends DataSourceException {

  private ErrorType errorType;

  public enum ErrorType {
    NETWORK {
      @Override public String getErrorCause() {
        return "Network error exception occurred!";
      }
    }, CONVERSION {
      @Override public String getErrorCause() {
        return "Conversion error exception occurred!";
      }
    }, HTTP {
      @Override public String getErrorCause() {
        return "HTTP exception error occurred!";
      }
    }, UNEXPECTED {
      @Override public String getErrorCause() {
        return "UNEXPECTED error ocurred!";
      }
    };

    public abstract String getErrorCause();
  }

  public static NetworkErrorException of(ErrorType type) {
    return new NetworkErrorException(type);
  }

  public NetworkErrorException(ErrorType errorType, String message) {
    super(message);
    this.errorType = errorType;
  }

  public NetworkErrorException(ErrorType errorType) {
    this(errorType, errorType.getErrorCause());
  }

  public ErrorType getErrorType() {
    return errorType;
  }

}
