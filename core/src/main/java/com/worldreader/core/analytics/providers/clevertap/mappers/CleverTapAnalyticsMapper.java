package com.worldreader.core.analytics.providers.clevertap.mappers;

import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.analytics.mapper.AnalyticsMapper;

import java.util.*;

public interface CleverTapAnalyticsMapper<T extends AnalyticsEvent> extends AnalyticsMapper<T, Map<String, Object>> {
  @Override Map<String, Object> transform(T event);
}
