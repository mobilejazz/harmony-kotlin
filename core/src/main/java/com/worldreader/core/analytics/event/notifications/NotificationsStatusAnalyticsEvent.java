package com.worldreader.core.analytics.event.notifications;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class NotificationsStatusAnalyticsEvent implements AnalyticsEvent {

  private final boolean enabled;
  private final String countryCode;

  public NotificationsStatusAnalyticsEvent(boolean enabled, String countryCode) {
    this.enabled = enabled;
    this.countryCode = countryCode;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getCountryCode() {
    return countryCode;
  }
}
