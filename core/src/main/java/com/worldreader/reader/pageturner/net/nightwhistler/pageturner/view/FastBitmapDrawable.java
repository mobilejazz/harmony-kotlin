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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.StreamingResource;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import org.javatuples.Triplet;

public class FastBitmapDrawable extends Drawable {

  private static final String TAG = FastBitmapDrawable.class.getSimpleName();

  private Bitmap bitmap;
  private final String resource;
  private final StreamingBookRepository dataSource;
  private final BookMetadata metadata;

  private boolean isLoaded = false;

  private int width;
  private int height;

  public FastBitmapDrawable(Bitmap b, String resource, final StreamingBookRepository dataSource, final BookMetadata metadata) {
    this.bitmap = b;
    this.resource = resource;
    this.dataSource = dataSource;
    this.metadata = metadata;
    if (b != null) {
      this.width = b.getWidth();
      this.height = b.getHeight();
    }
  }

  @Override public void draw(@NonNull Canvas canvas) {
    Log.d("FastBitmapDrawable", "Draw: FastBitmapDrawable: " + resource);
    if (bitmap != null) {
      canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);

      if (!isLoaded) {
        StreamingResource streamingResource;
        try {
          streamingResource = this.dataSource.getBookResource(metadata.getBookId(), metadata, URLDecoder.decode(resource));
        } catch (Throwable throwable) {
          streamingResource = StreamingResource.create(null);
        }

        final InputStream inputStream = streamingResource.getInputStream();

        try {
          final Bitmap bitmap = getBitmapOptimized(inputStream);
          if (bitmap == null || bitmap.getHeight() < 1 || bitmap.getWidth() < 1) {
            return;
          }
          destroy();
          this.bitmap = bitmap;
          isLoaded = true;
          invalidateSelf();
        } catch (OutOfMemoryError | IOException e) {
          Log.e(TAG, "Could not load image", e);
          isLoaded = true;
        }
      }
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

  @Nullable private Bitmap getBitmapOptimized(InputStream input) throws IOException {
    // First, let's decode image size (to avoid having the image loaded in memory
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;

    // Let's obtain the size of the bitmap image
    BitmapFactory.decodeStream(input, null, options);

    // Reset InputStream to beginning for decoding properly later the image
    input.reset();

    if (options.outHeight != -1 && options.outWidth != -1) {
      final int originalWidth = options.outWidth;
      final int originalHeight = options.outHeight;

      final Triplet<Integer, Integer, Boolean> targetSizes = calculateProperImageSize(originalWidth, originalHeight);
      final int targetWidth = targetSizes.getValue0();
      final int targetHeight = targetSizes.getValue1();
      final boolean isResized = targetSizes.getValue2();

      if (targetHeight == 0 || targetWidth == 0) {
        return null;
      }

      // Set properly the new sizes to be decoded
      options.outWidth = targetWidth;
      options.outHeight = targetHeight;

      // Allow to decode the whole InputStream
      options.inJustDecodeBounds = false;

      if (!isResized) {
        // Let's calculate insamplesize to resize the bitmap accordingly
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);

        // Let's try to return the most optimized version for the bitmap
        return BitmapFactory.decodeStream(input, null, options);
      } else {
        // Unluckily, we have to resize the Bitmap and for that we need to load Bitmap into memory :( (there's no other option
        final Bitmap originalBitmap = BitmapFactory.decodeStream(input);
        return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);
      }
    }

    return null;
  }

  public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }

  private Triplet<Integer, Integer, Boolean> calculateProperImageSize(int originalWidth, int originalHeight) {
    //final int screenHeight = getHeight() - (verticalMargin * 2);
    final int screenHeight = 1920;
    //final int screenWidth = getWidth() - (horizontalMargin * 2);
    final int screenWidth = 1080;

    final float ratio = (float) originalWidth / (float) originalHeight;

    int targetHeight = screenHeight - 1;
    int targetWidth = (int) (targetHeight * ratio);

    if (targetWidth > screenWidth - 1) {
      targetWidth = screenWidth - 1;
      targetHeight = (int) (targetWidth * (1 / ratio));
    }

    Log.d(TAG, "Rescaling from " + originalWidth + "x" + originalHeight + " to " + targetWidth + "x" + targetHeight);

    return Triplet.with(targetWidth, targetHeight, true);
  }
}
