package com.worldreader.core.analytics.event.reader;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class ReaderOpenReaderOptionsAnalyticsEvent implements AnalyticsEvent {

 private final String bookId;
 private final String bookTitle;
 private final String country;

  public ReaderOpenReaderOptionsAnalyticsEvent(String bookId, String bookTitle, String country) {
    this.bookId = bookId;
    this.bookTitle = bookTitle;
    this.country = country;
  }

  public String getBookId() {
    return bookId;
  }

  public String getBookTitle() {
    return bookTitle;
  }

  public String getCountry() {
    return country;
  }
}
