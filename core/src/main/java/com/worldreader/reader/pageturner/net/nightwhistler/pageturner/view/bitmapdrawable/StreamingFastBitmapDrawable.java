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
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.StreamingResource;
import com.worldreader.core.domain.repository.StreamingBookRepository;

import java.io.*;
import java.net.URLDecoder;
import java.util.concurrent.*;

public class StreamingFastBitmapDrawable extends AbstractFastBitmapDrawable {

  private static final String TAG = StreamingFastBitmapDrawable.class.getSimpleName();

  private static final String BACKGROUND_COLOR = "#fbf3ea";

  private final Handler handler = new Handler(Looper.getMainLooper());

  private final Rect r;
  private final Shape shape;
  private final Paint paint;

  private boolean isLoaded;
  private boolean isProcessing;
  private final StreamingBookRepository dataSource;
  private final BookMetadata metadata;
  private final String resource;
  private final Logger logger;

  public StreamingFastBitmapDrawable(int width, int height, BookMetadata metadata, StreamingBookRepository repository, String resource, Logger logger) {
    super(width, height);

    this.dataSource = repository;
    this.metadata = metadata;
    this.resource = resource;
    this.logger = logger;

    this.r = new Rect(0, 0, width, height);

    this.shape = new RectShape();
    shape.resize(width, height);

    this.paint = new Paint();
    paint.setColor(Color.parseColor(BACKGROUND_COLOR));

    setBounds(0, 0, width - 1, height - 1);
  }

  @Override public void draw(@NonNull Canvas canvas) {
    if (bitmap == null) {
      final int count = canvas.save();
      canvas.translate(r.left, r.top);
      shape.draw(canvas, paint);
      canvas.restoreToCount(count);

      if (!isLoaded && !isProcessing) {
        isProcessing = true;
        final ListenableFuture<StreamingResource> future = dataSource.getBookResourceFuture(metadata.bookId, metadata, URLDecoder.decode(resource));
        Futures.addCallback(future, new FutureCallback<StreamingResource>() {
          @Override public void onSuccess(final StreamingResource result) {
            final InputStream inputStream = result.getInputStream();
            handler.post(new Runnable() {
              @Override public void run() {
                generateDrawable(inputStream);
                isProcessing = false;
              }
            });
          }

          @Override public void onFailure(@NonNull final Throwable t) {
            if (t instanceof CancellationException) {
              return;
            }
            handler.post(new Runnable() {
              @Override public void run() {
                isProcessing = false;
                isLoaded = true;
              }
            });
          }
        }, MoreExecutors.directExecutor());
      }
    } else {
      canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
    }
  }

  private void generateDrawable(final InputStream inputStream) {
    try {
      final Bitmap localBitmap = decodeBitmap(inputStream);
      if (localBitmap == null || localBitmap.getHeight() < 1 || localBitmap.getWidth() < 1) {
        isLoaded = true;
        return;
      }
      recycle();
      bitmap = localBitmap;
      isLoaded = true;
      invalidateSelf();
    } catch (Throwable e) {
      logger.e(TAG, "Could not load image: " + Throwables.getStackTraceAsString(e));
      isLoaded = true;
    }
  }

  public void recycle() {
    if (this.bitmap != null) {
      this.bitmap.recycle();
    }

    this.bitmap = null;
  }

}
