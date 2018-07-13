package com.worldreader.core.analytics.event.reader;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class ReaderChangeFontSizeAnalyticsEvent implements AnalyticsEvent {

 private final String bookId;
 private final String bookTitle;
 private final String newFontSize;
 private final String country;

  public ReaderChangeFontSizeAnalyticsEvent(String bookId, String bookTitle, String newFontSize, String country) {
    this.bookId = bookId;
    this.bookTitle = bookTitle;
    this.newFontSize = newFontSize;
    this.country = country;
  }

  public String getBookId() {
    return bookId;
  }

  public String getBookTitle() {
    return bookTitle;
  }

  public String getNewFontSize() {
    return newFontSize;
  }
  public String getCountry() {
    return country;
  }
}
