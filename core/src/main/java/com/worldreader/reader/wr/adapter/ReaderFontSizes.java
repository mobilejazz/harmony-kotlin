package com.worldreader.reader.wr.adapter;

import android.support.annotation.StringRes;
import com.worldreader.core.R;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.FontSizes;

public enum ReaderFontSizes {
  SMALLEST(FontSizes.SMALLEST, R.string.ls_book_reading_font_size_smallest),
  SMALL(FontSizes.SMALL, R.string.ls_book_reading_font_size_small),
  NORMAL(FontSizes.NORMAL, R.string.ls_book_reading_font_size_normal),
  BIG(FontSizes.BIG, R.string.ls_book_reading_font_size_big),
  BIGGEST(FontSizes.BIGGEST, R.string.ls_book_reading_font_size_biggest);

  private int size; // in pt
  private int stringId;

  ReaderFontSizes(int size, @StringRes int stringId) {
    this.size = size;
    this.stringId = stringId;
  }

  public int getSize() {
    return size;
  }

  public int getStringId() {
    return stringId;
  }

  public static ReaderFontSizes getByFontSize(int fontsize) {
    for (ReaderFontSizes fontSize : values()) {
      if (fontSize.getSize() == fontsize) {
        return fontSize;
      }
    }
    return NORMAL;
  }
}
