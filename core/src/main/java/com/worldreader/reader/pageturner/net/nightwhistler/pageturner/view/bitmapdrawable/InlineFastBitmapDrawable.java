package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;

import java.io.*;

public class InlineFastBitmapDrawable extends AbstractFastBitmapDrawable {

  private static final String TAG = InlineFastBitmapDrawable.class.getSimpleName();

  private final InputStream is;
  private final Logger logger;

  public InlineFastBitmapDrawable(InputStream is, int width, int height, Logger logger) {
    super(width, height);
    this.is = is;
    this.logger = logger;
  }

  @Override public void draw(@NonNull Canvas canvas) {
    if (bitmap == null) {
      loadBitmap();
      if (bitmap == null) {
        return; // Something bad has happened
      }
    }
    canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
  }

  private void loadBitmap() {
    try {
      final Bitmap localBitmap = decodeBitmap(is);
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
