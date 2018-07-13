package com.worldreader.core.analytics.event.other;

import android.support.annotation.NonNull;
import com.worldreader.core.analytics.event.AnalyticsEvent;

public class AgeSelectedAnalyticsEvent implements AnalyticsEvent {

  private final String ageSelected;
  private final String country;

  public static AgeSelectedAnalyticsEvent of(String ageSelected, @NonNull String country) {
    return new AgeSelectedAnalyticsEvent(ageSelected, country);
  }

  public AgeSelectedAnalyticsEvent(String ageSelected, String country) {
    this.ageSelected = ageSelected;
    this.country = country;
  }

  public String getAgeSelected() {
    return ageSelected;
  }

  public String getCountry() {
    return country;
  }
}
