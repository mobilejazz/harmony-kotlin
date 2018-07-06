package com.worldreader.core.analytics.event.books;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class BookLikedAnalyticsEvent implements AnalyticsEvent {

  private String title;
  private int score;

  public static BookLikedAnalyticsEvent of(String title, int score) {
    return new BookLikedAnalyticsEvent(title, score);
  }

  private BookLikedAnalyticsEvent(String title, int score) {
    this.title = title;
    this.score = score;
  }

  public String getTitle() {
    return title;
  }

  public int getScore() {
    return score;
  }
}
