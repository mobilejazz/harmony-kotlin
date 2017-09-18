package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import com.worldreader.reader.epublib.nl.siegmann.epublib.Constants;
import com.worldreader.reader.epublib.nl.siegmann.epublib.service.MediatypeService;

import java.io.IOException;

/** A Resource that is inlined and does not depend on content outside. */
public class InlineResource extends Resource {

  public InlineResource(byte[] data, String href) {
    super(null, data, href, MediatypeService.determineMediaType(href), Constants.CHARACTER_ENCODING);
  }

  @Override public byte[] getData() throws IOException {
    return this.data;
  }

  @Override public void close() {
    this.data = null;
  }
}
