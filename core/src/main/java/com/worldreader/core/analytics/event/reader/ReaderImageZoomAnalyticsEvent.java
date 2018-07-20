package com.worldreader.core.analytics.event.reader;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class ReaderImageZoomAnalyticsEvent implements AnalyticsEvent {

 private final String bookId;
 private final String bookTitle;
 private final String imageName;
 private final String country;

  public ReaderImageZoomAnalyticsEvent(String bookId, String bookTitle, String imageName, String country) {
    this.bookId = bookId;
    this.bookTitle = bookTitle;
    this.imageName = imageName;
    this.country = country;
  }

  public String getBookId() {
    return bookId;
  }

  public String getBookTitle() {
    return bookTitle;
  }

  public String getImageName() {
    return imageName;
  }
  public String getCountry() {
    return country;
  }
}
