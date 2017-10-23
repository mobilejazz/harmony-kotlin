package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.StreamingResource;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

public class StreamingResourcesLoader implements ResourcesLoader {

  public static final String TAG = StreamingResourcesLoader.class.getSimpleName();

  private final BookMetadata bookMetadata;
  private final StreamingBookRepository dataSource;
  private final Logger logger;

  private final List<Holder> callbacks = new ArrayList<>();

  public StreamingResourcesLoader(BookMetadata bookMetadata, StreamingBookRepository dataSource, Logger logger) {
    this.bookMetadata = bookMetadata;
    this.dataSource = dataSource;
    this.logger = logger;
  }

  @Override public InputStream loadResource(String path) {
    StreamingResource bookResource;
    try {
      bookResource = dataSource.getBookResource(bookMetadata.getBookId(), bookMetadata, URLDecoder.decode(path));
    } catch (Throwable throwable) {
      bookResource = StreamingResource.create(null);
    }

    if (bookResource.getInputStream() == null) {
      logger.d(TAG, "Book resource is null with path: " + path);
    }

    return bookResource.getInputStream();
  }

  @Override public InputStream loadResource(Resource resource) {
    return this.loadResource(resource.getHref());
  }

  @Override public void onPrepareBitmapDrawables() {
    for (Holder holder : callbacks) {
      holder.callback.onPrepareFastBitmapDrawable(holder.href, dataSource, bookMetadata);
    }
  }

  @Override public void registerImageCallback(String resolvedHref, ImageResourceCallback imageCallback) {
    this.callbacks.add(new Holder(resolvedHref, imageCallback));
  }

  @Override public void clearImageResources() {
    this.callbacks.clear();
  }

  private static class Holder {

    String href;
    ImageResourceCallback callback;

    Holder(String href, ImageResourceCallback callback) {
      this.href = href;
      this.callback = callback;
    }

  }

}
