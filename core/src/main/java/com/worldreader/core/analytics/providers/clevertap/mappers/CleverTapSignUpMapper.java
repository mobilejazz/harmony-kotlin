package com.worldreader.core.analytics.providers.clevertap.mappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.worldreader.core.analytics.event.register.SignUpAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;

import java.util.*;

public class CleverTapSignUpMapper implements CleverTapAnalyticsMapper<SignUpAnalyticsEvent> {
  private final SharedPreferences preferences;

  public CleverTapSignUpMapper(Context context) {
    preferences = context.getSharedPreferences("wr-analytics", Context.MODE_PRIVATE);
  }

  @Override public Map<String, Object> transform(final SignUpAnalyticsEvent event) {
      return new HashMap<String, Object>() {{
        put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, CleverTapEventConstants.SING_UP_EVENT);
        put(CleverTapEventConstants.USER_NAME, event.getUsername());
        put(CleverTapEventConstants.REGISTER_ATTRIBUTE, event.getRegister());
        put(CleverTapEventConstants.USER_ID, preferences.getString("userId", "-1"));
        put(CleverTapEventConstants.DEVICE_ID, preferences.getString("deviceId", "-1"));
        put(CleverTapEventConstants.COUNTRY, "");//TODO
        put(CleverTapEventConstants.DEVICE_MANUFACTURER, Build.MANUFACTURER);
        put(CleverTapEventConstants.DEVICE_MODEL, Build.MODEL);
        put(CleverTapEventConstants.OS, Build.VERSION.RELEASE);
      }};
  }
}
