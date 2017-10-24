package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources;

import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;

import java.io.*;
import java.util.*;

public class FileEpubResourcesLoader implements ResourcesLoader {

  public static final String TAG = FileEpubResourcesLoader.class.getSimpleName();

  private final Logger logger;

  private final List<StreamingResourcesLoader.Holder> callbacks = new ArrayList<>();

  public FileEpubResourcesLoader(final Logger logger) {
    this.logger = logger;
  }

  @Override public InputStream loadResource(Resource resource) {
    try {
      return resource.getInputStream();
    } catch (IOException e) {
      logger.d(TAG, "Can't initialize properly the resource: " + Throwables.getStackTraceAsString(e));
      return null;
    }
  }

  @Override public void onPrepareBitmapDrawables() {
    for (Holder holder : callbacks) {
      holder.callback.onPrepareFastBitmapDrawable(holder.href, null, bookMetadata);
    }
  }

  @Override public void registerImageCallback(String resolvedHref, ImageResourceCallback imageCallback) {
    this.callbacks.add(new Holder(resolvedHref, imageCallback));
  }

  @Override public void clearImageResources() {
    this.callbacks.clear();
  }
}
