package com.worldreader.core.common.deprecated.error;

public class ErrorCore<T extends RuntimeException> {

  private T cause;
  private String errorMessage;

  public static final ErrorCore EMPTY = new ErrorCore();

  @SuppressWarnings("unchecked")
  public static <T extends RuntimeException> ErrorCore of(T cause, String errorMessage) {
    return new ErrorCore(cause, errorMessage);
  }

  @SuppressWarnings("unchecked")
  public static <T extends RuntimeException> ErrorCore of(T cause) {
    return new ErrorCore(cause);
  }

  private ErrorCore() {
  }

  private ErrorCore(T cause) {
    this(cause, null);
  }

  private ErrorCore(T cause, String errorMessage) {
    this.cause = cause;
    this.errorMessage = errorMessage;
  }

  public String getMessage() {
    return this.errorMessage;
  }

  public T getCause() {
    return this.cause;
  }

}
