package com.worldreader.core.analytics.event;

import java.util.*;

public class BasicAnalyticsEvent implements AnalyticsEvent {

  private final String eventName;
  private final Map<String, String> map;

  public BasicAnalyticsEvent(final String eventName, final Map<String, String> map) {
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
