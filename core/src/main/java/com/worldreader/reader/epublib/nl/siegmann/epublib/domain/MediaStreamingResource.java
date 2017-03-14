package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/** A resource that holds a media input but does not hold any data at all as is loaded differently. */
public class MediaStreamingResource extends StreamingResource {

  public MediaStreamingResource(String href) {
    super(href);
  }

  public MediaStreamingResource(String id, byte[] data, String href, MediaType mediaType) {
    super(id, data, href, mediaType);
  }

  @Override public void setData(byte[] data) {
  }

  @Override public void setData(InputStream inputStream) throws IOException {
  }

  @Override public byte[] getData() throws IOException {
    return null;
  }

  @Override public Reader getReader() throws IOException {
    return null;
  }
}
