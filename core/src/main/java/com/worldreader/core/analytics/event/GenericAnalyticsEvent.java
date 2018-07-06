package com.worldreader.core.analytics.event;

import dagger.internal.Preconditions;

import java.util.*;

@Deprecated
public class GenericAnalyticsEvent implements AnalyticsEvent {

  private final String eventName;
  private final Map<String, Object> values;

  public GenericAnalyticsEvent(final String eventName) {
    this.eventName = Preconditions.checkNotNull(eventName);
    this.values = Collections.emptyMap();
  }

  public GenericAnalyticsEvent(final String eventName, final Map<String, Object> values) {
    this.eventName = eventName;
    this.values = values;
  }

  public Map<String, Object> getValues() {
    return values;
  }

  public String getEventName() {
    return eventName;
  }

}
