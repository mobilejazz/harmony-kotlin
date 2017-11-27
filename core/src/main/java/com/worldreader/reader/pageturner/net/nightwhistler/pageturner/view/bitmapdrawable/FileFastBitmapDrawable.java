package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;

public class FileFastBitmapDrawable extends AbstractFastBitmapDrawable {

  private static final String TAG = FileFastBitmapDrawable.class.getSimpleName();

  private final Resource r;
  private final Logger logger;

  public FileFastBitmapDrawable(Resource r, int width, int height, Logger logger) {
    super(width, height);
    this.r = r;
    this.logger = logger;
  }

  @Override public void draw(@NonNull Canvas canvas) {
    //if (bitmap == null) {
    //  loadBitmap();
    //  if (bitmap == null) {
    //    return; // Something bad has happened
    //  }
    //}
    //canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
  }

  private void loadBitmap() {
    try {
      final Bitmap localBitmap = decodeBitmap(r.getInputStream());
      if (localBitmap == null || localBitmap.getHeight() < 1 || localBitmap.getWidth() < 1) {
        return;
      }
      recycle();
      bitmap = localBitmap;
      invalidateSelf();
    } catch (Throwable e) {
      logger.e(TAG, "Could not load image: " + Throwables.getStackTraceAsString(e));
    }
  }

}
