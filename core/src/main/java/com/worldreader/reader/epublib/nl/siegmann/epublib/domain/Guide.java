package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import java.io.*;
import java.util.*;

/**
 * The guide is a selection of special pages of the book.
 * Examples of these are the cover, list of illustrations, etc.
 * <p>
 * It is an optional part of an epub, and support for the various types of references varies by
 * reader.
 * <p>
 * The only part of this that is heavily used is the cover page.
 *
 * @author paul
 */
public class Guide implements Serializable {

  private static final long serialVersionUID = -6256645339915751189L;

  private static final int COVERPAGE_NOT_FOUND = -1;
  private static final int COVERPAGE_UNITIALIZED = -2;

  private List<GuideReference> references = new ArrayList<>();
  private int coverPageIndex = -1;

  public List<GuideReference> getReferences() {
    return references;
  }

  public void setReferences(List<GuideReference> references) {
    this.references = references;
    uncheckCoverPage();
  }

  private void uncheckCoverPage() {
    coverPageIndex = COVERPAGE_UNITIALIZED;
  }

  public GuideReference getCoverReference() {
    checkCoverPage();
    if (coverPageIndex >= 0) {
      return references.get(coverPageIndex);
    }
    return null;
  }

  public int setCoverReference(GuideReference guideReference) {
    if (coverPageIndex >= 0) {
      references.set(coverPageIndex, guideReference);
    } else {
      references.add(0, guideReference);
      coverPageIndex = 0;
    }
    return coverPageIndex;
  }

  private void checkCoverPage() {
    if (coverPageIndex == COVERPAGE_UNITIALIZED) {
      initCoverPage();
    }
  }

  private void initCoverPage() {
    int result = COVERPAGE_NOT_FOUND;
    for (int i = 0; i < references.size(); i++) {
      GuideReference guideReference = references.get(i);
      if (guideReference.getType().equals(GuideReference.COVER)) {
        result = i;
        break;
      }
    }
    coverPageIndex = result;
  }

  public Resource getCoverPage() {
    GuideReference guideReference = getCoverReference();
    if (guideReference == null) {
      return null;
    }
    return guideReference.getResource();
  }

  public ResourceReference addReference(GuideReference reference) {
    this.references.add(reference);
    uncheckCoverPage();
    return reference;
  }

  /**
   * A list of all GuideReferences that have the given referenceTypeName (ignoring case).
   */
  public List<GuideReference> getGuideReferencesByType(String referenceTypeName) {
    final List<GuideReference> result = new ArrayList<>();
    for (GuideReference guideReference : references) {
      if (referenceTypeName.equalsIgnoreCase(guideReference.getType())) {
        result.add(guideReference);
      }
    }
    return result;
  }
}