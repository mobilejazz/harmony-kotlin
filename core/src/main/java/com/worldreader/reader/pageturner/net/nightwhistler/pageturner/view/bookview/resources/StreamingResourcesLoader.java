package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.datasource.StreamingBookDataSource;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.StreamingResource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

class StreamingResourcesLoader implements ResourcesLoader {

  private static final String TAG = StreamingResourcesLoader.class.getSimpleName();

  private final BookMetadata bm;
  private final StreamingBookDataSource ds;
  private final Logger logger;

  private final List<ImageResourceCallback> callbacks = new ArrayList<>();

  public StreamingResourcesLoader(BookMetadata bm, StreamingBookDataSource ds, Logger logger) {
    this.bm = bm;
    this.ds = ds;
    this.logger = logger;
  }

  @Override public InputStream loadResource(Resource resource) {
    final String href = resource.getHref();

    StreamingResource bookResource;
    try {
      bookResource = ds.getBookResource(bm.bookId, bm, URLDecoder.decode(href));
    } catch (Throwable throwable) {
      bookResource = StreamingResource.create(null);
    }

    if (bookResource.getInputStream() == null) {
      logger.d(TAG, "Book resource is null with path: " + href);
    }

    return bookResource.getInputStream();
  }

  @Override public void onPrepareBitmapDrawables() {
    for (ImageResourceCallback holder : callbacks) {
      holder.onPrepareFastBitmapDrawable();
    }
  }

  @Override public void registerImageCallback(ImageResourceCallback callback) {
    this.callbacks.add(callback);
  }

  @Override public void clearImageResources() {
    this.callbacks.clear();
  }
}
