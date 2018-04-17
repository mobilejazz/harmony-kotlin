package net.nightwhistler.pageturner.view.bookview.resources;

import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import nl.siegmann.epublib.domain.Resource;

import java.io.*;
import java.util.*;

public class FileEpubResourcesLoader implements ResourcesLoader {

  public static final String TAG = FileEpubResourcesLoader.class.getSimpleName();

  private final List<ImageResourceCallback> callbacks = new ArrayList<>();
  private final Logger logger;

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
