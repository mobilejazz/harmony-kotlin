package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources;

import com.google.common.base.Throwables;
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

  @Override public InputStream loadResource(Resource resource) {
    try {
      final StreamingResource r = dataSource.getBookResource(bookMetadata.getBookId(), bookMetadata, URLDecoder.decode(resource.getHref()));
      return r.getInputStream();
    } catch (Throwable throwable) {
      logger.e(TAG, "Couldn't load the resource: " + resource.getHref() + " | Reason: " + Throwables.getStackTraceAsString(throwable));
      return null;
    }
  }

  @Override public void onPrepareBitmapDrawables() {
    for (Holder holder : callbacks) {
      holder.callback.onPrepareFastBitmapDrawable(holder.href);
    }
  }

  @Override public void registerImageCallback(String resolvedHref, ImageResourceCallback imageCallback) {
    this.callbacks.add(new Holder(resolvedHref, imageCallback));
  }

  @Override public void clearImageResources() {
    this.callbacks.clear();
  }

}
