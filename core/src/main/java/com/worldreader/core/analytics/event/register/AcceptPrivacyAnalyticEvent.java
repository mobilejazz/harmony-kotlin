package com.worldreader.core.analytics.event.register;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public class AcceptPrivacyAnalyticEvent implements AnalyticsEvent {

  private final String country;

  public AcceptPrivacyAnalyticEvent(){
    country = null;
  }

  public AcceptPrivacyAnalyticEvent(String country) {
    this.country = country;
  }

  public String getCountry() {
    return country;
  }

}
