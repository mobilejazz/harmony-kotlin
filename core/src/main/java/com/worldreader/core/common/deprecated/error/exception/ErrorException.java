package com.worldreader.core.common.deprecated.error.exception;

public class ErrorException extends RuntimeException {

  public ErrorException() {
  }

  public ErrorException(String detailMessage) {
    super(detailMessage);
  }

  public ErrorException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public ErrorException(Throwable throwable) {
    super(throwable);
  }
}
