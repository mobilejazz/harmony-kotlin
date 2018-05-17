package com.worldreader.core.analytics.providers.clevertap.mappers;

import com.worldreader.core.analytics.event.register.SignInAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;
import java.util.HashMap;
import java.util.Map;

public class CleverTapSignInMapper implements CleverTapAnalyticsMapper<SignInAnalyticsEvent> {

  @Override public Map<String, Object> transform(final SignInAnalyticsEvent event) {
    return new HashMap<String, Object>() {{
      put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, "UserSignIn");
      put("UserId", event.getUserId());
      //put("DeviceId", event.getDeviceId());
      put("Country", event.getCountry());
      put("Date", event.getDate());
      //put("Username", event.getUsername());
      put("Os", "Android");
    }};
  }
}
