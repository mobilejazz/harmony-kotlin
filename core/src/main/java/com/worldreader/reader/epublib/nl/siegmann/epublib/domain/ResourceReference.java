package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import java.io.*;

public class ResourceReference implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 2596967243557743048L;
  protected Resource resource;

  public ResourceReference(Resource resource) {
    this.resource = resource;
  }

  public Resource getResource() {
    return resource;
  }

  /**
   * Besides setting the resource it also sets the fragmentId to null.
   */
  public void setResource(Resource resource) {
    this.resource = resource;
  }

  /**
   * The id of the reference referred to.
   *
   * null of the reference is null or has a null id itself.
   */
  public String getResourceId() {
    if (resource != null) {
      return resource.getId();
    }
    return null;
  }
}
