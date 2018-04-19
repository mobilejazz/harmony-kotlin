package nl.siegmann.epublib.domain;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

/**
 * The spine sections are the sections of the book in the order in which the book should be read.
 * <p>
 * This contrasts with the Table of Contents sections which is an index into the Book's sections.
 */
public class Spine implements Serializable {

  private final List<SpineReference> spineReferences;
  private Resource tocResource;

  public Spine() {
    this(new ArrayList<SpineReference>());
  }

  public Spine(List<SpineReference> spineReferences) {
    this.spineReferences = spineReferences;
  }

  public List<SpineReference> getSpineReferences() {
    return spineReferences;
  }

  @Nullable public Resource getResource(int index) {
    if (index < 0 || index >= spineReferences.size()) {
      return null;
    }
    return spineReferences.get(index).getResource();
  }

  public void setTocResource(Resource tocResource) {
    this.tocResource = tocResource;
  }

  public Resource getTocResource() {
    return tocResource;
  }

  public int size() {
    return spineReferences.size();
  }

  public boolean isEmpty() {
    return spineReferences.isEmpty();
  }
}
