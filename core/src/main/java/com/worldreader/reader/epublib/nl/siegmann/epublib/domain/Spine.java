package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import java.io.*;
import java.util.*;

/**
 * The spine sections are the sections of the book in the order in which the book should be read.
 * <p>
 * This contrasts with the Table of Contents sections which is an index into the Book's sections.
 */
public class Spine implements Serializable {

  private static final long serialVersionUID = 3878483958947357246L;

  private Resource tocResource;
  private List<SpineReference> spineReferences;

  public Spine() {
    this(new ArrayList<SpineReference>());
  }

  public Spine(List<SpineReference> spineReferences) {
    this.spineReferences = spineReferences;
  }

  public List<SpineReference> getSpineReferences() {
    return spineReferences;
  }

  /**
   * Gets the resource at the given index.
   * Null if not found.
   */
  public Resource getResource(int index) {
    if (index < 0 || index >= spineReferences.size()) {
      return null;
    }
    return spineReferences.get(index).getResource();
  }

  /**
   * The number of elements in the spine.
   */
  public int size() {
    return spineReferences.size();
  }

  /**
   * As per the epub file format the spine officially maintains a reference to the Table of
   * Contents.
   * The epubwriter will look for it here first, followed by some clever tricks to find it elsewhere
   * if not found.
   * Put it here to be sure of the expected behaviours.
   */
  public void setTocResource(Resource tocResource) {
    this.tocResource = tocResource;
  }

  public Resource getTocResource() {
    return tocResource;
  }

  public boolean isEmpty() {
    return spineReferences.isEmpty();
  }
}
