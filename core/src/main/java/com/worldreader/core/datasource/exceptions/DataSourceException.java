package com.worldreader.core.datasource.exceptions;

/** Generic DataSource exception. */
public class DataSourceException extends RuntimeException {

  public DataSourceException() {
    super();
  }

  public DataSourceException(String detailMessage) {
    super(detailMessage);
  }

  public DataSourceException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public DataSourceException(Throwable throwable) {
    super(throwable);
  }

}
