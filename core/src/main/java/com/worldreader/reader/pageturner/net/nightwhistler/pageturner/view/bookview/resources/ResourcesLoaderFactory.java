package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources;

import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.reader.wr.fragments.AbstractReaderFragment;

public class ResourcesLoaderFactory {

  private ResourcesLoaderFactory() {
    throw new AssertionError("No instances of this class!");
  }

  public static ResourcesLoader create(final BookMetadata m, final AbstractReaderFragment.DICompanion di) {
    final int mode = m.mode;
    switch (mode) {
      case BookMetadata.FILE_MODE:
        return new FileEpubResourcesLoader(di.logger);
      case BookMetadata.STREAMING_MODE:
        // TODO: 22/11/2017 See if we need to implement StreamingResourcesLoader
        return null; //new StreamingResourcesLoader(bookMetadata, di.streamingBookDataSource, di.logger);
      default:
        throw new IllegalStateException("BookMetadata mode not recognized!");
    }
  }
}
