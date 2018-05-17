package com.worldreader.core.analytics.providers.clevertap.mappers;

import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.analytics.event.books.BookDetailAnalyticsEvent;
import com.worldreader.core.analytics.event.books.BookReadAnalyticsEvent;
import com.worldreader.core.analytics.event.books.BookStartReadingAnalyticsEvent;
import com.worldreader.core.analytics.event.register.SignInAnalyticsEvent;
import com.worldreader.core.analytics.event.register.SignUpAnalyticsEvent;
import com.worldreader.core.analytics.mapper.AnalyticsEventMappers;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CleverTapAnalyticsEventMappers implements AnalyticsEventMappers<CleverTapAnalyticsMapper<? extends AnalyticsEvent>> {

  public static final CleverTapAnalyticsMapper<AnalyticsEvent> NONE = new CleverTapAnalyticsMapper<AnalyticsEvent>() {
    @Override public Map<String, Object> transform(AnalyticsEvent event) {
      return Collections.emptyMap();
    }
  };

  private final Map<Class<?>, CleverTapAnalyticsMapper<? extends AnalyticsEvent>> mappers;

  public CleverTapAnalyticsEventMappers() {
    this.mappers = createMappers();
  }

  private Map<Class<?>, CleverTapAnalyticsMapper<? extends AnalyticsEvent>> createMappers() {
    final Map<Class<?>, CleverTapAnalyticsMapper<? extends AnalyticsEvent>> m = new HashMap<Class<?>, CleverTapAnalyticsMapper<? extends AnalyticsEvent>>() {{
      put(BookDetailAnalyticsEvent.class, new CleverTapBookDetailMapper());
      put(BookStartReadingAnalyticsEvent.class, new CleverTapBookStartReadingMapper());
      put(BookReadAnalyticsEvent.class, new CleverTapBookReadMapper());
      put(SignUpAnalyticsEvent.class, new CleverTapSignUpMapper());
      put(SignInAnalyticsEvent.class, new CleverTapSignInMapper());
    }};
    return Collections.unmodifiableMap(m);
  }

  @Override public CleverTapAnalyticsMapper<? extends AnalyticsEvent> obtain(Class<?> clazz) {
    return mappers.get(clazz);
  }
}
