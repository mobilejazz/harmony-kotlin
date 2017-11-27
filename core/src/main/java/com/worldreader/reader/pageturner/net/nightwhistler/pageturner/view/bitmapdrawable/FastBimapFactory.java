package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;

public class FastBimapFactory {

  private FastBimapFactory() {
    throw new AssertionError("No instances allowed!");
  }

  public static AbstractFastBitmapDrawable create(final BookMetadata bm, final Resource r, final int width, final int height, final Logger logger) {
    final int mode = bm.mode;
    switch (mode) {
      case BookMetadata.FILE_MODE:
        return new FileFastBitmapDrawable(r, width, height, logger);
      case BookMetadata.STREAMING_MODE:
        //return new StreamingFastBitmapDrawable(width, height, bm, repository, data, logger); todo
        return null;
      default:
        throw new IllegalStateException("BookMetadata mode not recognized!");
    }
  }

}
