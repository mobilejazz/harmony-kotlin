package nl.siegmann.epublib.domain;

import java.io.*;
import java.util.*;

public class TOCReference extends TitledResourceReference implements Serializable {

  private List<TOCReference> children;

  public TOCReference(String name, Resource resource) {
    this(name, resource, null);
  }

  public TOCReference(String name, Resource resource, String fragmentId) {
    this(name, resource, fragmentId, new ArrayList<TOCReference>());
  }

  private TOCReference(String title, Resource resource, String fragmentId, List<TOCReference> children) {
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

    final String thisId = getResourceId();

    final TOCReference other = (TOCReference) o;
    final String otherId = other.getResourceId();

    return thisId != null && thisId.equalsIgnoreCase(otherId);
  }
}
