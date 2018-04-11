package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.mobilejazz.logger.library.Logger;
import nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueuedTask;

import javax.annotation.Nullable;
import java.util.concurrent.*;

// TODO: Removed harcoded reference to QueuedTask executor and pass it over constructor
public class FileFastBitmapDrawable extends AbstractFastBitmapDrawable {

  private static final String TAG = FileFastBitmapDrawable.class.getSimpleName();

  private static final String BACKGROUND_COLOR = "#fbf3ea";

  private final Handler handler = new Handler(Looper.getMainLooper());

  private final Rect rect;
  private final Shape shape;
  private final Paint paint;

  private final Resource r;
  private final Logger logger;

  private boolean decodeError;

  public FileFastBitmapDrawable(Resource r, int width, int height, Logger logger) {
    super(width, height);
    this.r = r;
    this.logger = logger;

    this.rect = new Rect(0, 0, width, height);

    this.shape = new RectShape();
    shape.resize(width, height);

    this.paint = new Paint();
    paint.setColor(Color.parseColor(BACKGROUND_COLOR));

    setBounds(0, 0, width - 1, height - 1);
  }

  @Override public void draw(@NonNull Canvas canvas) {
    if (bitmap == null) {
      // Draw initial shape
      final int count = canvas.save();
      canvas.translate(rect.left, rect.top);
      shape.draw(canvas, paint);
      canvas.restoreToCount(count);

      // Try to load the bitmap
      if (!decodeError) {
        loadBitmap();
      }
    } else {
      canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
    }
  }

  private void loadBitmap() {
    final ListenableFuture<Bitmap> future = QueuedTask.READER_THREAD_EXECUTOR.submit(new Callable<Bitmap>() {
      @Override public Bitmap call() throws Exception {
        return decodeBitmap(r.getInputStream());
      }
    });
    Futures.addCallback(future, new FutureCallback<Bitmap>() {
      @Override public void onSuccess(@Nullable final Bitmap result) {
        handler.post(new Runnable() {
          @Override public void run() {
            displayBitmap(result);
          }
        });
      }

      @Override public void onFailure(@NonNull Throwable t) {
        handler.post(new Runnable() {
          @Override public void run() {
            // Mark this one as error
            decodeError = true;
          }
        });
      }
    }, MoreExecutors.directExecutor());
  }

  @Override public void destroy() {
    decodeError = false;
    super.destroy();
  }

  @Override public void recycleForReuse() {
    recycle();
  }

  private void displayBitmap(Bitmap b) {
    try {
      if (b == null || b.getHeight() < 1 || b.getWidth() < 1) {
        return;
      }
      recycle();
      bitmap = b;
      invalidateSelf();
    } catch (Throwable e) {
      logger.e(TAG, "Could not load image: " + Throwables.getStackTraceAsString(e));
    }
  }
}
