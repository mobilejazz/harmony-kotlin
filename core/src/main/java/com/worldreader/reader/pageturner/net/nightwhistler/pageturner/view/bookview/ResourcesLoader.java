package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;

import java.io.*;

public interface ResourcesLoader {

  interface ImageResourceCallback {
    void onLoadImageResource(String href, InputStream stream, StreamingBookRepository datasource, BookMetadata bookMetadata);
  }

  InputStream loadResource(String path);

  InputStream loadResource(Resource resource);

  void loadImageResources();

  void registerImageCallback(String resolvedHref, ImageResourceCallback imageCallback);
}
