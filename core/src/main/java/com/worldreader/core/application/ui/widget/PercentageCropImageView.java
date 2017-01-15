package com.worldreader.core.application.ui.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/*
 * Adapted from ImageView code at:
 * http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.4.4_r1/android/widget/ImageView.java
 */
public class PercentageCropImageView extends ImageView {

  private Float mCropYCenterOffsetPct;
  private Float mCropXCenterOffsetPct;

  public PercentageCropImageView(Context context) {
    super(context);
  }

  public PercentageCropImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PercentageCropImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public float getCropYCenterOffsetPct() {
    return mCropYCenterOffsetPct;
  }

  public void setCropYCenterOffsetPct(float cropYCenterOffsetPct) {
    if (cropYCenterOffsetPct > 1.0) {
      throw new IllegalArgumentException("Value too large: Must be <= 1.0");
    }
    this.mCropYCenterOffsetPct = cropYCenterOffsetPct;
  }

  public float getCropXCenterOffsetPct() {
    return mCropXCenterOffsetPct;
  }

  public void setCropXCenterOffsetPct(float cropXCenterOffsetPct) {
    if (cropXCenterOffsetPct > 1.0) {
      throw new IllegalArgumentException("Value too large: Must be <= 1.0");
    }
    this.mCropXCenterOffsetPct = cropXCenterOffsetPct;
  }

  private void myConfigureBounds() {
    if (this.getScaleType() == ScaleType.MATRIX) {
            /*
             * Taken from Android's ImageView.java implementation:
             *
             * Excerpt from their source:
    } else if (ScaleType.CENTER_CROP == mScaleType) {
       mDrawMatrix = mMatrix;

       float scale;
       float dx = 0, dy = 0;

       if (dwidth * vheight > vwidth * dheight) {
           scale = (float) vheight / (float) dheight;
           dx = (vwidth - dwidth * scale) * 0.5f;
       } else {
           scale = (float) vwidth / (float) dwidth;
           dy = (vheight - dheight * scale) * 0.5f;
       }

       mDrawMatrix.setScale(scale, scale);
       mDrawMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
    }
             */

      Drawable d = this.getDrawable();
      if (d != null) {
        int dwidth = d.getIntrinsicWidth();
        int dheight = d.getIntrinsicHeight();

        Matrix m = new Matrix();

        int vwidth = getWidth() - this.getPaddingLeft() - this.getPaddingRight();
        int vheight = getHeight() - this.getPaddingTop() - this.getPaddingBottom();

        float scale;
        float dx = 0, dy = 0;

        if (dwidth * vheight > vwidth * dheight) {
          float cropXCenterOffsetPct =
              mCropXCenterOffsetPct != null ? mCropXCenterOffsetPct.floatValue() : 0.5f;
          scale = (float) vheight / (float) dheight;
          dx = (vwidth - dwidth * scale) * cropXCenterOffsetPct;
        } else {
          float cropYCenterOffsetPct =
              mCropYCenterOffsetPct != null ? mCropYCenterOffsetPct.floatValue() : 0f;

          scale = (float) vwidth / (float) dwidth;
          dy = (vheight - dheight * scale) * cropYCenterOffsetPct;
        }

        m.setScale(scale, scale);
        m.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));

        this.setImageMatrix(m);
      }
    }
  }

  // These 3 methods call configureBounds in ImageView.java class, which
  // adjusts the matrix in a call to center_crop (android's built-in
  // scaling and centering crop method). We also want to trigger
  // in the same place, but using our own matrix, which is then set
  // directly at line 588 of ImageView.java and then copied over
  // as the draw matrix at line 942 of ImageVeiw.java
  @Override protected boolean setFrame(int l, int t, int r, int b) {
    boolean changed = super.setFrame(l, t, r, b);
    this.myConfigureBounds();
    return changed;
  }

  @Override public void setImageDrawable(Drawable d) {
    super.setImageDrawable(d);
    this.myConfigureBounds();
  }

  @Override public void setImageResource(int resId) {
    super.setImageResource(resId);
    this.myConfigureBounds();
  }

  public void redraw() {
    Drawable d = this.getDrawable();

    if (d != null) {
      // Force toggle to recalculate our bounds
      this.setImageDrawable(null);
      this.setImageDrawable(d);
    }
  }
}
