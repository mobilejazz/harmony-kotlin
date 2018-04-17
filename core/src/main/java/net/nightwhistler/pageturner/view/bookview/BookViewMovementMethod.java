package net.nightwhistler.pageturner.view.bookview;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.span.ClickableImageSpan;

public class BookViewMovementMethod extends LinkMovementMethod {

  @Override public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
    final int action = event.getAction();

    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
      final int x = (int) event.getX() - widget.getTotalPaddingLeft() + widget.getScrollX();
      final int y = (int) event.getY() - widget.getTotalPaddingTop() + widget.getScrollY();

      final Layout layout = widget.getLayout();

      // Using the y position, find the line
      final int line = getLineForVertical(layout, y);
      if (line == -1) {
        return true;
      }

      // Find position on the y line (after finding the x one)
      final int offset = layout.getOffsetForHorizontal(line, x);

      // Links have more priority than images
      final ClickableSpan[] links = buffer.getSpans(offset, offset, ClickableSpan.class);
      if (links.length != 0) {
        if (action == MotionEvent.ACTION_UP) {
          links[0].onClick(widget);
        } else {
          Selection.setSelection(buffer, buffer.getSpanStart(links[0]), buffer.getSpanEnd(links[0]));
        }
        return true;
      }

      final ClickableImageSpan[] imageSpans = buffer.getSpans(offset, offset, ClickableImageSpan.class);
      if (action == MotionEvent.ACTION_UP && imageSpans.length != 0) {
        imageSpans[0].onClick(widget);
        Selection.removeSelection(buffer);
      }
    }

    return true;
  }

  /**
   * Get the line number corresponding to the specified y position.
   * If you ask for a position above 0, you get 0; if you ask for a position
   * below the bottom of the text, you get -1.
   *
   * Contrary to what Layout.getLineForVertical does (which is returning the last line)
   */
  private int getLineForVertical(Layout l, int y) {
    int high = l.getLineCount();
    int low = -1;
    int guess;

    final int height = l.getHeight();
    if (y > height) {
      return -1;
    }

    while (high - low > 1) {
      guess = (high + low) / 2;

      final int top = l.getLineTop(guess);
      if (top > y) {
        high = guess;
      } else {
        low = guess;
      }
    }

    return low < 0 ? 0 : low;
  }

}
