package com.worldreader.core.domain.model;

import java.io.*;

public class StreamingResource {

  public static final StreamingResource EMPTY = new StreamingResource(new ByteArrayInputStream(new byte[] {}));

  private final InputStream inputStream;

  private StreamingResource(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public InputStream getInputStream() {
    return inputStream;
  }

  public static StreamingResource create(InputStream inputStream) {
    return new StreamingResource(inputStream);
  }

}
