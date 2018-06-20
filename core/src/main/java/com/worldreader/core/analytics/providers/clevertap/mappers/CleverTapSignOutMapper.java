package com.worldreader.core.analytics.providers.clevertap.mappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.worldreader.core.analytics.event.register.SignOutAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;

import java.util.*;

public class CleverTapSignOutMapper implements CleverTapAnalyticsMapper<SignOutAnalyticsEvent> {
  private final SharedPreferences preferences;

  public CleverTapSignOutMapper(Context context) {
    preferences = context.getSharedPreferences("wr-preferences", Context.MODE_PRIVATE);
  }

  @Override public Map<String, Object> transform(final SignOutAnalyticsEvent event) {
    return new HashMap<String, Object>() {{
      put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, CleverTapEventConstants.SIGNOUT_EVENT);
      put(CleverTapEventConstants.USER_NAME, event.getUsername());
      put(CleverTapEventConstants.REGISTER_ATTRIBUTE, event.getRegister());
      put(CleverTapEventConstants.IDENTITY, event.getUserId() != null ? event.getUserId() : preferences.getString("userId", "-1"));
      put(CleverTapEventConstants.USER_ID, event.getUserId() != null ? event.getUserId() : preferences.getString("userId", "-1"));
      put(CleverTapEventConstants.DEVICE_ID, preferences.getString("deviceId", "-1"));
      put(CleverTapEventConstants.LANGUAGE_ATTRIBUTE, event.getLanguage());
      put(CleverTapEventConstants.COUNTRY, event.getCountry());
      put(CleverTapEventConstants.DEVICE_MANUFACTURER, Build.MANUFACTURER);
      put(CleverTapEventConstants.DEVICE_MODEL, Build.MODEL);
      put(CleverTapEventConstants.OS, Build.VERSION.RELEASE);
    }};
  }
}
