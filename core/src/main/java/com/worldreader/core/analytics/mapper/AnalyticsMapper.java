package com.worldreader.core.analytics.mapper;

import com.worldreader.core.analytics.event.AnalyticsEvent;

public interface AnalyticsMapper<T extends AnalyticsEvent, K> {

  K transform(T event);

}
