package com.worldreader.core.analytics.event;

import java.util.*;

public class SimpleAnalyticsEvent implements AnalyticsEvent {

  private final String eventName;
  private final Map<String, String> map;

  public SimpleAnalyticsEvent(final String eventName, final Map<String, String> map) {
    this.eventName = eventName;
    this.map = map;
  }

  public Map<String, String> getMap() {
    return map;
  }

  public String getEventName() {
    return eventName;
  }
}
