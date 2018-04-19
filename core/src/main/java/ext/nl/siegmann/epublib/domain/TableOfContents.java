package nl.siegmann.epublib.domain;

import java.io.*;
import java.util.*;

/**
 * The table of contents of the book.
 * The TableOfContents is a tree structure at the root it is a list of TOCReferences, each if which
 * may have as children another list of TOCReferences.
 * <p>
 * The table of contents is used by epub as a quick index to chapters and sections within chapters.
 * It may contain duplicate entries, may decide to point not to certain chapters, etc.
 * <p>
 * See the spine for the complete list of sections in the order in which they should be read.
 *
 * @see Spine
 */
public class TableOfContents implements Serializable {

  private final Set<TOCReference> tocReferences;

  TableOfContents() {
    this(new ArrayList<TOCReference>());
  }

  public TableOfContents(List<TOCReference> tocReferences) {
    this.tocReferences = tocReferences == null ? new LinkedHashSet<TOCReference>() : new LinkedHashSet<>(tocReferences);
  }

  public List<TOCReference> getTocReferences() {
    return new ArrayList<>(tocReferences);
  }

  /**
   * All unique references (unique by href) in the order in which they are referenced to in the
   * table of contents.
   */
  public List<Resource> getAllUniqueResources() {
    final Set<String> uniqueHrefs = new HashSet<>();
    final List<Resource> result = new ArrayList<>();
    final ArrayList<TOCReference> references = new ArrayList<>(tocReferences);
    getAllUniqueResources(uniqueHrefs, result, references);
    return result;
  }

  private static void getAllUniqueResources(Set<String> uniqueHrefs, List<Resource> result, List<TOCReference> tocReferences) {
    for (TOCReference tocReference : tocReferences) {
      Resource resource = tocReference.getResource();
      if (resource != null && !uniqueHrefs.contains(resource.getHref())) {
        uniqueHrefs.add(resource.getHref());
        result.add(resource);
      }
      getAllUniqueResources(uniqueHrefs, result, tocReference.getChildren());
    }
  }

  public int size() {
    return getTotalSize(tocReferences);
  }

  private static int getTotalSize(Collection<TOCReference> tocReferences) {
    int result = tocReferences.size();
    for (TOCReference tocReference : tocReferences) {
      result += getTotalSize(tocReference.getChildren());
    }
    return result;
  }

  public TOCReference addTOCReference(TOCReference tocReference) {
    tocReferences.remove(tocReference);
    tocReferences.add(tocReference);
    return tocReference;
  }
}
