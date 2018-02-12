package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import java.io.*;
import java.util.*;

public class TOCReference extends TitledResourceReference implements Serializable {

  private static final long serialVersionUID = 5787958246077042456L;

  private List<TOCReference> children;

  public TOCReference(String name, Resource resource) {
    this(name, resource, null);
  }

  public TOCReference(String name, Resource resource, String fragmentId) {
    this(name, resource, fragmentId, new ArrayList<TOCReference>());
  }

  public TOCReference(String title, Resource resource, String fragmentId, List<TOCReference> children) {
    super(resource, title, fragmentId);
    this.children = children;
  }

  public List<TOCReference> getChildren() {
    return children;
  }

  public void setChildren(List<TOCReference> children) {
    this.children = children;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final TOCReference tocReference = (TOCReference) o;
    return this.getResource().getId().equals(tocReference.getResource().getId());
  }
}
