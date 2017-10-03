/*
 * Copyright (C) 2008 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class FastBitmapDrawable extends Drawable {

  private static final String TAG = FastBitmapDrawable.class.getSimpleName();

  private final String resource;
  private final StreamingBookRepository dataSource;
  private final BookMetadata metadata;
  private final Logger logger;

  private final Handler handler = new Handler(Looper.getMainLooper());

  private final Rect r;
  private final Shape shape;
  private final Paint paint;

  private final int width;
  private final int height;

  private Bitmap bitmap;
  private boolean isLoaded = false;
  private boolean isProcessing = false;

  public FastBitmapDrawable(final Context context, final String resource, final int width, final int height, final StreamingBookRepository dataSource, final BookMetadata metadata, final Logger logger) {
    this.resource = resource;
    this.dataSource = dataSource;
    this.metadata = metadata;

    this.width = width;
    this.height = height;
    this.logger = logger;

    this.r = new Rect(0, 0, width, height);

    this.shape = new RectShape();
    shape.resize(width, height);

    this.paint = new Paint();
    paint.setColor(Color.parseColor("#fbf3ea"));

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
        final ListenableFuture<StreamingResource> future = dataSource.getBookResourceFuture(metadata.getBookId(), metadata, URLDecoder.decode(resource));
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

  @Override public void setAlpha(int alpha) {
  }

  @Override public void setColorFilter(ColorFilter cf) {
  }

  @Override public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  @Override public int getIntrinsicWidth() {
    return width;
  }

  @Override public int getIntrinsicHeight() {
    return height;
  }

  @Override public int getMinimumWidth() {
    return width;
  }

  @Override public int getMinimumHeight() {
    return height;
  }

  private void generateDrawable(final InputStream inputStream) {
    try {
      final Bitmap localBitmap = getBitmap(inputStream);
      if (localBitmap == null || localBitmap.getHeight() < 1 || localBitmap.getWidth() < 1) {
        return;
      }
      destroy();
      bitmap = localBitmap;
      isLoaded = true;
      invalidateSelf();
    } catch (Throwable e) {
      logger.e(TAG, "Could not load image: " + Throwables.getStackTraceAsString(e));
      isLoaded = true;
    }
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public void destroy() {
    if (this.bitmap != null) {
      this.bitmap.recycle();
    }

    this.bitmap = null;
    //this.setCallback(null);
  }

  @Nullable private Bitmap getBitmap(InputStream is) throws IOException {
    final Bitmap originalBitmap = BitmapFactory.decodeStream(is);
    return Bitmap.createScaledBitmap(originalBitmap, width, height, true);
  }

}
