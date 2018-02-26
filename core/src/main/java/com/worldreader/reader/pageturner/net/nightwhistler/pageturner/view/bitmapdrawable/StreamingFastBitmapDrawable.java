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
import android.text.TextUtils;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.StreamingResource;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueuedTask;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URLDecoder;
import java.util.concurrent.*;

// TODO: 28/11/2017 If possible do a refactor to include loading logic inside Resource instead of passing variables around here
public class StreamingFastBitmapDrawable extends AbstractFastBitmapDrawable {

  private static final String TAG = StreamingFastBitmapDrawable.class.getSimpleName();

  private static final String BACKGROUND_COLOR = "#fbf3ea";

  private final Handler handler = new Handler(Looper.getMainLooper());

  private final Rect r;
  private final Shape shape;
  private final Paint paint;

  private final StreamingBookRepository repository;
  private final BookMetadata bm;
  private final Resource resource;
  private final Logger logger;

  private volatile ListenableFuture<StreamingResource> future;
  private volatile boolean isLoaded;
  private volatile boolean isProcessing;

  public StreamingFastBitmapDrawable(int width, int height, BookMetadata metadata, StreamingBookRepository repository, Resource resource, Logger logger) {
    super(width, height);

    this.repository = repository;
    this.bm = metadata;
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
        retrieveDrawableResource();
      }
    } else {
      canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
    }
  }

  private void retrieveDrawableResource() {
    isProcessing = true;
    if (bm.mode == BookMetadata.FILE_MODE) {
      retrieveDrawableFromResource();
    } else {
      retrieveDrawableFromRepository();
    }
  }

  private void retrieveDrawableFromResource() {
    // TODO: This should be passed as constructor to be completely agnostic
    final ListenableFuture<InputStream> future = QueuedTask.READER_THREAD_EXECUTOR.submit(new Callable<InputStream>() {
      @Override public InputStream call() throws Exception {
        return null;
      }
    });
    Futures.addCallback(future, new FutureCallback<InputStream>() {
      @Override public void onSuccess(@Nullable InputStream result) {
        convertToDrawable(result);
      }

      @Override public void onFailure(Throwable t) {
        markAsError(t);
      }
    }, MoreExecutors.directExecutor());
  }

  private void retrieveDrawableFromRepository() {
    final String href = resource.getHref();
    if (!TextUtils.isEmpty(href)) {
      final String uri = URLDecoder.decode(href);
      this.future = repository.getBookResourceFuture(bm.bookId, bm, uri);
      Futures.addCallback(future, new FutureCallback<StreamingResource>() {
        @Override public void onSuccess(final StreamingResource result) {
          final InputStream inputStream = result.getInputStream();
          convertToDrawable(inputStream);
        }

        @Override public void onFailure(@NonNull final Throwable t) {
          markAsError(t);
        }
      }, MoreExecutors.directExecutor());
    }
  }

  // WorkerThread
  private void markAsError(@NonNull Throwable t) {
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

  // Worker Thread
  private void convertToDrawable(InputStream inputStream) {
    generateDrawable(inputStream);
    handler.post(new Runnable() {
      @Override public void run() {
        // UI Thread
        isProcessing = false;
        future = null;
        invalidateSelf();
      }
    });
  }

  // WorkerThread
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

  @Override public void recycleForReuse() {
    if (future != null) {
      future.cancel(true);
      future = null;
    }
    recycle();
    isLoaded = false;
    isProcessing = false;
  }

}
