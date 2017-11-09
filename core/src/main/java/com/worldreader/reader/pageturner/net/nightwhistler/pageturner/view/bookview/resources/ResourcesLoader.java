package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources;

import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;

import java.io.*;

public interface ResourcesLoader {

  InputStream loadResource(Resource resource);

  void onPrepareBitmapDrawables();

  void registerImageCallback(ImageResourceCallback callback);

  void clearImageResources();
}
