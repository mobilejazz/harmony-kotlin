package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.*;

public class BookNavigationGestureDetector extends GestureDetector.SimpleOnGestureListener {

  private static final String TAG = BookNavigationGestureDetector.class.getSimpleName();

  //Distance to scroll 1 unit on edge slide.
  private static final int SCROLL_FACTOR = 50;
  private static final int BOOKVIEW_BLOCK_TIME = 1500;

  private BookViewListener bookViewListener;
  private BookView bookView;
  private DisplayMetrics metrics;

  public BookNavigationGestureDetector(final BookView bookView, final DisplayMetrics metrics, final BookViewListener navListener) {
    this.bookView = bookView;
    this.metrics = metrics;
    this.bookViewListener = navListener;
  }

  @Override public boolean onSingleTapUp(MotionEvent e) {
    bookView.blockInnerViewFor(BOOKVIEW_BLOCK_TIME);

    if (bookViewListener.onPreScreenTap()) {
      return true;
    }

    final float x = e.getX();
    final float y = e.getY();

    // First we check if we want to scroll the page

    final int TAP_RANGE_H = bookView.getWidth() / 5;

    if (x < TAP_RANGE_H) {
      return bookViewListener.onTapLeftEdge();
    } else if (x > bookView.getWidth() - TAP_RANGE_H) {
      return bookViewListener.onTapRightEdge();
    }

    // If not, maybe the user wants to click on a link
    //final List<ClickableSpan> spans = bookView.getLinkAt(x, y);
    //
    //Log.d(TAG, "Got " + spans.size() + " ClickableSpans.");
    //
    //if (!spans.isEmpty()) {
    //  for (ClickableSpan span : spans) {
    //    span.onClick(bookView);
    //  }
    //
    //  return true;
    //}

    this.bookViewListener.onScreenTap();

    return false;
  }

  @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    bookViewListener.onPreSlide();

    float scrollUnitSize = SCROLL_FACTOR * metrics.density;

    final int TAP_RANGE_H = bookView.getWidth() / 5;
    float delta = (e1.getY() - e2.getY()) / scrollUnitSize;
    int level = (int) delta;

    if (e1.getX() < TAP_RANGE_H) {
      return this.bookViewListener.onLeftEdgeSlide(level);
    } else if (e1.getX() > bookView.getWidth() - TAP_RANGE_H) {
      return this.bookViewListener.onRightEdgeSlide(level);
    }

    return super.onScroll(e1, e2, distanceX, distanceY);
  }

  @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    float distanceX = e2.getX() - e1.getX();
    float distanceY = e2.getY() - e1.getY();

    if (Math.abs(distanceX) > Math.abs(distanceY)) {
      bookView.blockInnerViewFor(BOOKVIEW_BLOCK_TIME);

      if (distanceX > 0) {
        return bookViewListener.onSwipeRight();
      } else {
        return bookViewListener.onSwipeLeft();
      }
    } else if (Math.abs(distanceY) > Math.abs(distanceX)) {
      bookView.blockInnerViewFor(BOOKVIEW_BLOCK_TIME);
    }

    return false;
  }

  @Override public void onLongPress(MotionEvent e) {
  }
}
