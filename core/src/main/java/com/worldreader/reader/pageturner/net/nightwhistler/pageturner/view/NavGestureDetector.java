/*
 * Copyright (C) 2012 Alex Kuiper
 * 
 * This file is part of PageTurner
 *
 * PageTurner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PageTurner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PageTurner.  If not, see <http://www.gnu.org/licenses/>.*
 */

package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view;

import android.os.Build;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.Configuration;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.BookView;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.BookViewListener;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.SelectedWord;
import jedi.functional.Command;
import jedi.functional.Command0;
import jedi.option.Option;

import java.util.*;

/**
 * Translates low-level touch and gesture events into more high-level
 * navigation events.
 *
 * @author Alex Kuiper
 */
public class NavGestureDetector extends GestureDetector.SimpleOnGestureListener {

  private static final String TAG = NavGestureDetector.class.getSimpleName();

  //Distance to scroll 1 unit on edge slide.
  private static final int SCROLL_FACTOR = 50;

  private static final int BOOKVIEW_BLOCK_TIME = 1500;

  private BookViewListener bookViewListener;
  private BookView bookView;
  private DisplayMetrics metrics;

  public NavGestureDetector(BookView bookView, BookViewListener navListener,
      DisplayMetrics metrics) {
    this.bookView = bookView;
    this.bookViewListener = navListener;
    this.metrics = metrics;
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
    final List<ClickableSpan> spans = bookView.getLinkAt(x, y);

    Log.d(TAG, "Got " + spans.size() + " ClickableSpans.");

    if (!spans.isEmpty()) {
      for (ClickableSpan span : spans) {
        span.onClick(bookView);
      }

      return true;
    }

    this.bookViewListener.onScreenTap();

    return false;
  }

  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
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

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
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
    //On older platforms we generate a popup-event.
    if (Build.VERSION.SDK_INT < Configuration.TEXT_SELECTION_PLATFORM_VERSION) {
      Option<SelectedWord> wordOption = bookView.getWordAt(e.getX(), e.getY());

      wordOption.match(new Command<SelectedWord>() {
        @Override public void execute(SelectedWord word) {
          bookViewListener.onWordLongPressed(word.getStartOffset(), word.getEndOffset(),
              word.getText());
        }
      }, new Command0() {
        @Override public void execute() {
        }
      });

      super.onLongPress(e);
    }
  }
}
