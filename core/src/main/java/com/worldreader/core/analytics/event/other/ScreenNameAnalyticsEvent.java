package com.worldreader.core.analytics.event.other;

import android.support.annotation.NonNull;
import com.worldreader.core.analytics.event.AnalyticsEvent;

public class ScreenNameAnalyticsEvent implements AnalyticsEvent {

  private final String screenName;
  private final String countryCode;

  public static ScreenNameAnalyticsEvent of(@NonNull String screenName, String countryCode) {

    return new ScreenNameAnalyticsEvent(screenName, countryCode);
  }

  private ScreenNameAnalyticsEvent(String screenName,String countryCode) {

    this.screenName = screenName;
    this.countryCode = countryCode;
  }

  public String getScreenName() {
    return screenName;
  }

  public String getCountryCode() {
    return countryCode;
  }
}
