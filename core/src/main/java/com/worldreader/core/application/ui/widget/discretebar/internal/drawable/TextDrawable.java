package com.worldreader.core.application.ui.widget.discretebar.internal.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class TextDrawable extends Drawable {

  private final CharSequence text;
  private final Layout textLayout;
  private final Rect textBounds;
  private final TextPaint textPaint;

  public TextDrawable(Context context, CharSequence text, float textSize, int textColor) {
    super();

    this.text = text;

    // Text Paint
    textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    textPaint.density = context.getResources().getDisplayMetrics().density;
    textPaint.setDither(true);
    textPaint.setTextAlign(Paint.Align.CENTER);
    textPaint.setTextSize(textSize);
    textPaint.setColor(textColor);
    textPaint.setFakeBoldText(true);

    // Text Bounds
    textBounds = new Rect();

    // Measure bounds
    double desired = Math.ceil(Layout.getDesiredWidth(text, textPaint));
    textLayout =
        new StaticLayout(text, textPaint, (int) desired, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f,
            false);
    textBounds.set(0, 0, textLayout.getWidth(), textLayout.getHeight());
  }

  @Override public void draw(Canvas canvas) {
    final Rect bounds = getBounds();
    final int count = canvas.save();
    canvas.translate(bounds.left, bounds.bottom);
    textLayout.draw(canvas);
    canvas.restoreToCount(count);
  }

  @Override public void setAlpha(int alpha) {
    textPaint.setAlpha(alpha);
  }

  @Override public void setColorFilter(ColorFilter colorFilter) {

  }

  @Override public int getOpacity() {
    return textPaint.getAlpha();
  }

  @Override protected void onBoundsChange(Rect bounds) {
    textBounds.set(bounds);
  }

  @Override public int getIntrinsicHeight() {
    if (textBounds.isEmpty()) {
      return -1;
    } else {
      return (textBounds.bottom - textBounds.top);
    }
  }

  @Override public int getIntrinsicWidth() {
    if (textBounds.isEmpty()) {
      return -1;
    } else {
      return (textBounds.right - textBounds.left);
    }
  }

  public void setTextSize(float size) {
    textPaint.setTextSize(size);
  }

}
