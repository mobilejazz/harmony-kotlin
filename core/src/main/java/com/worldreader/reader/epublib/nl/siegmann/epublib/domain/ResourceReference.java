package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

public abstract class ResourceReference implements Serializable {

  protected Resource resource;

  ResourceReference(Resource resource) {
    this.resource = resource;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  @Nullable public String getResourceId() {
    return resource != null ? resource.getId() : null;
  }

  @Override public int hashCode() {
    return 31 + (resource == null ? 0 : resource.hashCode());
  }
}
