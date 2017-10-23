package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources;

import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;

import java.io.*;

public interface ResourcesLoader {

  InputStream loadResource(String path);

  InputStream loadResource(Resource resource);

  void onPrepareBitmapDrawables();

  void registerImageCallback(String resolvedHref, ImageResourceCallback imageCallback);

  void clearImageResources();

  interface ImageResourceCallback {

    void onPrepareFastBitmapDrawable(String resourceHref, StreamingBookRepository datasource, BookMetadata bookMetadata);
  }
}
