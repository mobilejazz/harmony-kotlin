package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import java.io.*;

/**
 * A Section of a book.
 * Represents both an item in the package document and a item in the index.
 */
public class SpineReference extends ResourceReference implements Serializable {

  public SpineReference(Resource resource) {
    super(resource);
  }

}
