package com.worldreader.core.analytics.event.search;

import android.support.annotation.NonNull;
import com.worldreader.core.analytics.event.AnalyticsEvent;

public class SearchQueryAnalyticsEvent implements AnalyticsEvent {

  private final String query;

  public static SearchQueryAnalyticsEvent of(@NonNull String query) {
    return new SearchQueryAnalyticsEvent(query);
  }

  private SearchQueryAnalyticsEvent(String query) {
    this.query = query;
  }

  public String getQuery() {
    return query;
  }

}
