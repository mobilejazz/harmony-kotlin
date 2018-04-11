package nl.siegmann.epublib.domain;

import java.io.*;
import java.util.*;

public class Guide implements Serializable {

  private static final long serialVersionUID = -6256645339915751189L;

  private static final int COVER_PAGE_NOT_FOUND = -1;
  private static final int COVER_PAGE_UNITIALIZED = -2;

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
    coverPageIndex = COVER_PAGE_UNITIALIZED;
  }

  public GuideReference getCoverReference() {
    checkCoverPage();
    if (coverPageIndex >= 0) {
      return references.get(coverPageIndex);
    }
    return null;
  }

  private void checkCoverPage() {
    if (coverPageIndex == COVER_PAGE_UNITIALIZED) {
      initCoverPage();
    }
  }

  private void initCoverPage() {
    int result = COVER_PAGE_NOT_FOUND;
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
    final GuideReference guideReference = getCoverReference();
    if (guideReference == null) {
      return null;
    }
    return guideReference.getResource();
  }
}