package nl.siegmann.epublib.domain;

import java.io.*;

public class StreamingResource extends Resource {

  public StreamingResource(final String id, String href, MediaType mediaType) {
    super(id, null, href, mediaType);
  }

  @Override public byte[] getData() throws IOException {
    return super.data;
  }

  @Override public void close() {
    super.data = null;
  }
}
