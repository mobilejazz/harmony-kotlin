package com.worldreader.core.error.general;

public class UnexpectedErrorException extends RuntimeException {

  public UnexpectedErrorException() {
  }

  public UnexpectedErrorException(String detailMessage) {
    super(detailMessage);
  }

  public UnexpectedErrorException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public UnexpectedErrorException(Throwable throwable) {
    super(throwable);
  }

}
