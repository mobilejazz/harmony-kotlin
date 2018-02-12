package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.helper;

import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
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
      final StringBuilder builder = new StringBuilder();

      for (int i = 0; i < level; i++) {
        builder.append("  ");
      }

      final String title = ref.getTitle();
      builder.append(title);

      final Resource resource = ref.getResource();
      if (resource != null) {
        final String completeHref = ref.getCompleteHref();
        //final String resolvedTocHref = spine.resolveTocHref(completeHref);
        final String href = completeHref;
        final TocEntry tocEntry = new TocEntry(builder.toString(), href);
        entries.add(tocEntry);
      }

      final List<TOCReference> children = ref.getChildren();
      flatten(spine, children, entries, level + 1);
    }
  }

}
