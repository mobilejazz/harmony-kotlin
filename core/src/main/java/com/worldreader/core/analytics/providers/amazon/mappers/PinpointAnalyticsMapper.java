package com.worldreader.core.analytics.providers.amazon.mappers;

import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.analytics.mapper.AnalyticsMapper;

public interface PinpointAnalyticsMapper<T extends AnalyticsEvent>
    extends AnalyticsMapper<T, com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent> {

  @Override com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent transform(T event);
}
