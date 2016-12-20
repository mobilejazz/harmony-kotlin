package com.worldreader.core.datasource.model;

import java.io.*;

public class StreamingResourceEntity {

  private InputStream inputStream;

  private StreamingResourceEntity(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public InputStream getInputStream() {
    return inputStream;
  }

  public static StreamingResourceEntity create(InputStream inputStream) {
    return new StreamingResourceEntity(inputStream);
  }
}
