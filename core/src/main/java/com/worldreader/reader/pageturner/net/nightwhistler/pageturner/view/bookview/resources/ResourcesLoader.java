package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources;

import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;

import java.io.*;

public interface ResourcesLoader {

  InputStream loadResource(Resource resource);

  void onPrepareBitmapDrawables();

  void registerImageCallback(String resolvedHref, ImageResourceCallback imageCallback);

  void clearImageResources();

  interface ImageResourceCallback {

    void onPrepareFastBitmapDrawable(String resourceHref);
  }

  class Holder {

    String href;
    ImageResourceCallback callback;

    Holder(String href, ImageResourceCallback callback) {
      this.href = href;
      this.callback = callback;
    }
  }
}
