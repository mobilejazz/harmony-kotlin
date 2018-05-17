package com.worldreader.core.analytics.providers.amazon.mappers;

import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.analytics.event.other.SetUserIdAnalyticsEvent;
import com.worldreader.core.analytics.mapper.AnalyticsEventMappers;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PinpointAnalyticsEventMappers implements AnalyticsEventMappers<PinpointAnalyticsMapper<? extends AnalyticsEvent>> {

  public static final PinpointAnalyticsMapper<AnalyticsEvent> NONE = new PinpointAnalyticsMapper<AnalyticsEvent>() {
    @Override public com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent transform(AnalyticsEvent event) {
      return null;
    }
  };

  private final Map<Class<?>, PinpointAnalyticsMapper<? extends AnalyticsEvent>> mappers;

  public PinpointAnalyticsEventMappers() {
    this.mappers = createMappers();
  }

  private Map<Class<?>, PinpointAnalyticsMapper<? extends AnalyticsEvent>> createMappers() {

    final Map<Class<?>, PinpointAnalyticsMapper<? extends AnalyticsEvent>> mappers =
        new HashMap<Class<?>, PinpointAnalyticsMapper<? extends AnalyticsEvent>>() {{
          put(SetUserIdAnalyticsEvent.class, NONE);
        }};

    return Collections.unmodifiableMap(mappers);
  }

  @Override public PinpointAnalyticsMapper<?> obtain(Class<?> clazz) {
    return mappers.get(clazz);
  }
}
