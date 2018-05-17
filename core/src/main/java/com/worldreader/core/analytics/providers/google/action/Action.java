package com.worldreader.core.analytics.providers.google.action;

import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.analytics.mapper.AnalyticsMapper;

public interface Action<Sender, Event extends AnalyticsEvent, Mapper extends AnalyticsMapper<?, ?>, Value> {

  Value apply(Sender s, Mapper m, Event e);

}
