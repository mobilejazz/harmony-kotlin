package com.worldreader.core.analytics.event.reader;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class ReaderBookPageViewAnalyticsEvent implements AnalyticsEvent {

  public enum Action {
    NUMBER_PAGES,
    SECOND_PAGE
  }

  private final Action action;
  private final String title;
  private final int currentScrolledPages;

  public static ReaderBookPageViewAnalyticsEvent of(final Action action, final String title, final int currentScrolledPages) {
    return new ReaderBookPageViewAnalyticsEvent(action, title, currentScrolledPages);
  }

  private ReaderBookPageViewAnalyticsEvent(Action action, String title, int currentScrolledPages) {
    this.action = action;
    this.title = title;
    this.currentScrolledPages = currentScrolledPages;
  }

  public Action getAction() {
    return action;
  }

  public String getTitle() {
    return title;
  }

  public int getCurrentScrolledPages() {
    return currentScrolledPages;
  }

}
