package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.changestrategy;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.TextView;
import com.google.common.base.Throwables;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable.AbstractFastBitmapDrawable;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.BookView;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.span.ClickableImageSpan;
import jedi.option.Option;

import java.util.*;

import static java.util.Collections.*;
import static jedi.option.Options.*;

public class FixedPagesStrategy implements PageChangeStrategy {

  private static final String TAG = FixedPagesStrategy.class.getSimpleName();

  private final List<Integer> pageOffsets = new ArrayList<>();

  private Spanned text;
  private int page;

  private BookView bookView;
  private TextView childView;

  private int position = -1;

  public FixedPagesStrategy() {
  }

  public int getCurrentPage() {
    return page;
  }

  public List<Integer> getPageOffsets() {
    return new ArrayList<>(pageOffsets);
  }

  public List<Integer> getPageOffsets(CharSequence text) {
    if (text == null) {
      return emptyList();
    }

    final List<Integer> pageOffsets = new ArrayList<>();

    final TextView innerView = bookView.getInnerView();
    final TextPaint textPaint = innerView.getPaint();
    final int measuredWidth = innerView.getMeasuredWidth();
    final int measuredHeight = bookView.getMeasuredHeight();
    final int lineSpacing = bookView.getLineSpacing();

    final StaticLayout layout = safeCreateStaticLayout(text, textPaint, measuredWidth, lineSpacing);
    if (layout == null) {
      return emptyList();
    }

    final int verticalMargin = bookView.getVerticalMargin();
    final int paddingTop = bookView.getPaddingTop();
    final int paddingBottom = bookView.getPaddingBottom();

    layout.draw(new Canvas());

    //Subtract the height of the top margin and the padding
    final int pageHeight = measuredHeight - verticalMargin - paddingTop - verticalMargin - paddingBottom;

    Log.d(TAG, "Page height: " + pageHeight);

    int totalLines = layout.getLineCount();
    int topLineNextPage = -1;
    int pageStartOffset = 0;

    while (topLineNextPage < totalLines - 1) {
      Log.d(TAG, "Processing line " + topLineNextPage + " / " + totalLines);

      final int topLine = layout.getLineForOffset(pageStartOffset);
      topLineNextPage = layout.getLineForVertical(layout.getLineTop(topLine) + pageHeight);

      Log.d(TAG, "topLine " + topLine + " / " + topLineNextPage);
      if (topLineNextPage == topLine) { //If lines are bigger than can fit on a page
        topLineNextPage = topLine + 1;
      }

      final int pageEnd = layout.getLineEnd(topLineNextPage - 1);

      Log.d(TAG, "pageStartOffset=" + pageStartOffset + ", pageEnd=" + pageEnd);

      if (pageEnd > pageStartOffset) {
        if (text.subSequence(pageStartOffset, pageEnd).toString().trim().length() > 0) {
          pageOffsets.add(pageStartOffset);
        }
        pageStartOffset = layout.getLineStart(topLineNextPage);
      }
    }

    return pageOffsets;
  }

  private void updatePageNumber() {
    for (int i = 0; i < pageOffsets.size(); i++) {
      if (pageOffsets.get(i) > position) {
        page = i - 1;
        return;
      }
    }
    page = pageOffsets.size() - 1;
  }

  private Option<CharSequence> getTextForPage(int page) {
    if (pageOffsets.size() < 1 || page < 0) {
      return none();
    } else if (page >= pageOffsets.size() - 1) {
      final int startOffset = pageOffsets.get(pageOffsets.size() - 1);

      if (startOffset >= 0 && startOffset <= text.length() - 1) {
        return some(applySpans(text.subSequence(startOffset, text.length())));
      } else {
        return some(applySpans(text));
      }
    } else {
      int start = pageOffsets.get(page);
      int end = pageOffsets.get(page + 1);
      return some(applySpans(text.subSequence(start, end)));
    }
  }

  private CharSequence applySpans(CharSequence text) {
    return text;
  }

  @Override public void loadText(Spanned text) {
    this.text = text;
    page = 0;
    pageOffsets.clear();
    pageOffsets.addAll(getPageOffsets(text));
  }

  public int getTopLeftPosition() {
    if (pageOffsets.isEmpty()) {
      return 0;
    }

    if (page >= pageOffsets.size()) {
      return pageOffsets.get(pageOffsets.size() - 1);
    }

    return pageOffsets.get(page);
  }

  public int getProgressPosition() {
    if (position > 0 || pageOffsets.isEmpty() || page == -1) {
      return position;
    }

    return getTopLeftPosition();
  }

  public boolean isAtStart() {
    return page == 0;
  }

  public boolean isAtEnd() {
    return page == pageOffsets.size() - 1;
  }

  @Override public void setPosition(int pos) {
    this.position = pos;
  }

  @Override public void setRelativePosition(double relativePosition) {
    final int position = (int) Math.floor((int) (text.length() * relativePosition));
    setPosition(position);
  }

  @Override public void pageUp() {
    position = -1;

    if (isAtStart()) {
      final PageTurnerSpine spine = bookView.getSpine();
      if (spine == null || !spine.navigateBack()) {
        return;
      }
      clearText();
      position = Integer.MAX_VALUE;
      bookView.loadText();
    } else {
      page = Math.max(page - 1, 0);
      updatePosition();
    }
  }

  @Override public void pageDown() {
    position = -1;

    if (isAtEnd()) {
      final PageTurnerSpine spine = bookView.getSpine();
      if (spine == null || !spine.navigateForward()) {
        return;
      }
      clearText();
      page = 0;
      bookView.loadText();
    } else {
      page = Math.min(page + 1, pageOffsets.size() - 1);
      updatePosition();
    }
  }

  public boolean isScrolling() {
    return false;
  }

  @Override public void clearText() {
    text = new SpannableStringBuilder("");
    childView.setText(text);
    pageOffsets.clear();
  }

  @Override public void clearStoredPosition() {
    page = 0;
    position = 0;
  }

  @Override public void updatePosition() {
    if (pageOffsets.isEmpty() || text.length() == 0 || page == -1) {
      return;
    }

    if (position != -1) {
      updatePageNumber();
    }

    CharSequence sequence = getTextForPage(page).getOrElse("");
    if (sequence.length() > 0) {
      // #555 Remove \n at the end of sequence which get InnerView size changed
      int endIndex = sequence.length();
      while (sequence.charAt(endIndex - 1) == '\n') {
        endIndex--;
      }

      sequence = sequence.subSequence(0, endIndex);
    }

    recycleBitmapsIfNecessary();

    try {
      childView.setText(sequence);
    } catch (IndexOutOfBoundsException ie) {
      //If we get an error setting the formatted text, strip formatting and try again.
      Log.e(TAG, "IndexOutOfBoundsException while updating position:" + Throwables.getStackTraceAsString(ie));
      childView.setText(sequence.toString());
    }
  }

  private void recycleBitmapsIfNecessary() {
    final CharSequence text = childView.getText();
    if (text instanceof Spanned) {
      final Spanned spanned = (Spanned) text;
      final int length = text.length() - 1;
      final ClickableImageSpan[] spans = spanned.getSpans(0, length, ClickableImageSpan.class);
      for (ClickableImageSpan imageSpan : spans) {
        final Drawable drawable = imageSpan.getDrawable();
        if (drawable instanceof AbstractFastBitmapDrawable) {
          final AbstractFastBitmapDrawable fbmp = (AbstractFastBitmapDrawable) drawable;
          fbmp.recycleForReuse();
        }
      }
    }
  }

  @Override public void reset() {
    clearStoredPosition();
    pageOffsets.clear();
    clearText();
  }

  public Option<Spanned> getText() {
    return option(text);
  }

  @Override public void setBookView(BookView bookView) {
    this.bookView = bookView;
    childView = bookView.getInnerView();
  }

  @Override public int getSizeChartDisplayed() {
    if (childView != null && childView.getText() != null) {
      return childView.getText().length();
    } else {
      return 0;
    }
  }

  public StaticLayout safeCreateStaticLayout(CharSequence source, TextPaint paint, int width, float spacing) {
    try {
      return doCreateLayout(source, paint, width, spacing);
    } catch (IndexOutOfBoundsException e) {
      // Work-around for a Jelly bean bug: See http://code.google.com/p/android/issues/detail?id=35466
      return doCreateLayout(source.toString(), paint, width, spacing);
    }
  }

  private StaticLayout doCreateLayout(CharSequence source, TextPaint paint, int width, float spacing) {
    return new StaticLayout(source, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, spacing, true);
  }

}
