package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.span.ClickableImageSpan;

public class BookViewMovementMethod extends LinkMovementMethod {

  private static BookViewMovementMethod INSTANCE;

  public static BookViewMovementMethod getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new BookViewMovementMethod();
    }
    return INSTANCE;
  }

  @Override public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
    final int action = event.getAction();

    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
      int x = (int) event.getX();
      int y = (int) event.getY();

      x -= widget.getTotalPaddingLeft();
      y -= widget.getTotalPaddingTop();

      x += widget.getScrollX();
      y += widget.getScrollY();

      final Layout layout = widget.getLayout();
      int line = layout.getLineForVertical(y);
      int off = layout.getOffsetForHorizontal(line, x);

      ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
      ClickableImageSpan[] imageSpans = buffer.getSpans(off, off, ClickableImageSpan.class);

      if (link.length != 0) {
        if (action == MotionEvent.ACTION_UP) {
          link[0].onClick(widget);
        } else if (action == MotionEvent.ACTION_DOWN) {
          Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
        }

        return true;
      } else if (imageSpans.length != 0) {
        if (action == MotionEvent.ACTION_UP) {
          imageSpans[0].onClick(widget);
        } else if (action == MotionEvent.ACTION_DOWN) {
          Selection.setSelection(buffer, buffer.getSpanStart(imageSpans[0]), buffer.getSpanEnd(imageSpans[0]));
        }

        return true;
      } else {
        Selection.removeSelection(buffer);
      }
    }

    return true;
  }

}
