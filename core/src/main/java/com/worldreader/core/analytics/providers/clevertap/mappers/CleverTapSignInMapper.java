package com.worldreader.core.analytics.providers.clevertap.mappers;

import com.worldreader.core.analytics.event.register.SignInAnalyticsEvent;
import java.util.Collections;
import java.util.Map;

public class CleverTapSignInMapper implements CleverTapAnalyticsMapper<SignInAnalyticsEvent> {

  @Override public Map<String, Object> transform(SignInAnalyticsEvent event) {
    return Collections.emptyMap();
  }
}
