package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.span;

import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.view.View;

public class ClickableImageSpan extends ImageSpan {

  private final ClickableImageSpanListener listener;

  public ClickableImageSpan(final Drawable d, final ClickableImageSpanListener listener) {
    super(d);
    this.listener = listener;
  }

  public void onClick(final View widget) {
    if (listener != null) {
      final Drawable drawable = getDrawable();
      if (drawable != null) {
        listener.onImageClick(widget, drawable);
      }
    }
  }

  public interface ClickableImageSpanListener {

    void onImageClick(final View v, final Drawable drawable);

  }
}
