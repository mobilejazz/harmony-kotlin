package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable;

import com.worldreader.core.domain.model.BookMetadata;

public class FastBimapFactory {

  private FastBimapFactory() {
    throw new AssertionError("No instances allowed!");
  }

  public static AbstractFastBitmapDrawable create(final BookMetadata bm) {
    final int mode = bm.mode;
    switch (mode) {
      case BookMetadata.FILE_MODE:
        return null;
      case BookMetadata.STREAMING_MODE:
        return null;
      default:
        throw new IllegalStateException("BookMetadata mode not recognized!");
    }
  }

}
