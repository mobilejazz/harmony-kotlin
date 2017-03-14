package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import java.io.IOException;

public class StreamingResource extends Resource {

  public StreamingResource(String href) {
    super(href);
  }

  public StreamingResource(String id, byte[] data, String href, MediaType mediaType) {
    super(id, data, href, mediaType);
  }

  @Override public byte[] getData() throws IOException {
    return this.data;
  }

  @Override public void close() {
    this.data = null;
  }
}
