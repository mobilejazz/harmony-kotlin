package net.nightwhistler.pageturner.view.bitmapdrawable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import java.io.*;

public abstract class AbstractFastBitmapDrawable extends Drawable {

  protected final int width;
  protected final int height;

  protected Bitmap bitmap;

  public AbstractFastBitmapDrawable(final int width, final int height) {
    this.width = width;
    this.height = height;
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

  public void recycle() {
    if (this.bitmap != null) {
      this.bitmap.recycle();
    }

    this.bitmap = null;
  }

  public void destroy() {
    if ( this.bitmap != null ) {
      this.bitmap.recycle();
    }

    this.bitmap = null;
    this.setCallback(null);
  }

  public abstract void recycleForReuse();

  @Nullable protected Bitmap decodeBitmap(InputStream is) throws IOException {
    final Bitmap originalBitmap = BitmapFactory.decodeStream(is);
    return Bitmap.createScaledBitmap(originalBitmap, width, height, true);
  }
}
