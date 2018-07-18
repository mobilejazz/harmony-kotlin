package com.worldreader.core.analytics.event.reader;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class ReaderChangeFontTypeAnalyticsEvent implements AnalyticsEvent {

 private final String bookId;
 private final String bookTitle;
 private final String newFont;
 private final String country;

  public ReaderChangeFontTypeAnalyticsEvent(String bookId, String bookTitle, String newFont, String country) {
    this.bookId = bookId;
    this.bookTitle = bookTitle;
    this.newFont = newFont;
    this.country = country;
  }

  public String getBookId() {
    return bookId;
  }

  public String getBookTitle() {
    return bookTitle;
  }

  public String getNewFont() {
    return newFont;
  }

  public String getCountry() {
    return country;
  }
}
