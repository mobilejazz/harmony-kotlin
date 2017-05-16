package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.span;

import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.view.View;

public class ClickableImageSpan extends ImageSpan {

  private ClickableImageSpanListener onClickListener;

  public ClickableImageSpan(final Drawable d) {
    super(d);
  }

  public void onClick(final View widget) {
    if (onClickListener != null) {
      final Drawable drawable = getDrawable();
      if (drawable != null) {
        onClickListener.onImageClick(widget, drawable);
      }
    }
  }

  public void setOnClickListener(final ClickableImageSpanListener onClickListener) {
    this.onClickListener = onClickListener;
  }

  public interface ClickableImageSpanListener {

    void onImageClick(final View v, final Drawable drawable);

  }
}
