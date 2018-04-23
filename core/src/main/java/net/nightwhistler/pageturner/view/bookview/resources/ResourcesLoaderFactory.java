package net.nightwhistler.pageturner.view.bookview.resources;

import com.worldreader.core.domain.model.BookMetadata;
import net.nightwhistler.pageturner.view.bookview.DICompanion;

public class ResourcesLoaderFactory {

  private ResourcesLoaderFactory() {
    throw new AssertionError("No instances of this class!");
  }

  public static ResourcesLoader create(final BookMetadata bm, final DICompanion di) {
    final int mode = bm.mode;
    switch (mode) {
      case BookMetadata.FILE_MODE:
        return new FileEpubResourcesLoader(di.logger);
      case BookMetadata.STREAMING_MODE:
        return new StreamingResourcesLoader(bm, di.streamingBookDataSource, di.logger);
      default:
        throw new IllegalStateException("BookMetadata mode not recognized!");
    }
  }
}
