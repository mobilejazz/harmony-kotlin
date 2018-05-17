package com.worldreader.core.analytics.event.other;

import android.support.annotation.NonNull;
import com.worldreader.core.analytics.event.AnalyticsEvent;

public class ScreenNameAnalyticsEvent implements AnalyticsEvent {

  private final String screenName;

  public static ScreenNameAnalyticsEvent of(@NonNull String screenName) {
    return new ScreenNameAnalyticsEvent(screenName);
  }

  private ScreenNameAnalyticsEvent(String screenName) {
    this.screenName = screenName;
  }

  public String getScreenName() {
    return screenName;
  }
}
