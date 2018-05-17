package com.worldreader.core.analytics.providers.clevertap.mappers;

import com.worldreader.core.analytics.event.register.SignUpAnalyticsEvent;
import java.util.Collections;
import java.util.Map;

public class CleverTapSignUpMapper implements CleverTapAnalyticsMapper<SignUpAnalyticsEvent> {

  @Override public Map<String, Object> transform(SignUpAnalyticsEvent event) {
    return Collections.emptyMap();
  }
}
