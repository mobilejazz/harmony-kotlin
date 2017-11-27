package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.helper;

import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TOCReference;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TableOfContents;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.TocEntry;

import java.util.*;

public class TocUtils {

  private TocUtils() {
    throw new AssertionError("No instances allowed!");
  }

  public static List<TocEntry> flattenTocReferences(final PageTurnerSpine spine, final TableOfContents toc) {
    final List<TOCReference> tocReferences = toc.getTocReferences();
    final List<TocEntry> entries = new ArrayList<>();
    flatten(spine, tocReferences, entries, 0);
    return entries;
  }

  private static void flatten(final PageTurnerSpine spine, final List<TOCReference> refs, final List<TocEntry> entries, final int level) {
    if (spine == null || refs == null || refs.isEmpty()) {
      return;
    }

    for (TOCReference ref : refs) {
      StringBuilder title = new StringBuilder();

      for (int i = 0; i < level; i++) {
        title.append("  ");
      }

      title.append(ref.getTitle());

      if (ref.getResource() != null) {
        entries.add(new TocEntry(title.toString(), spine.resolveTocHref(ref.getCompleteHref())));
      }

      flatten(spine, ref.getChildren(), entries, level + 1);
    }
  }

}
