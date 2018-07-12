package com.worldreader.core.analytics.providers.clevertap.mappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.worldreader.core.analytics.event.other.UILanguageAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;

import java.util.*;

public class CleverTapUILanguageMapper implements CleverTapAnalyticsMapper<UILanguageAnalyticsEvent> {

  private final SharedPreferences preferences;

  public CleverTapUILanguageMapper(Context context) {
    preferences = context.getSharedPreferences("wr-preferences", Context.MODE_PRIVATE);
  }

  @Override public Map<String, Object> transform(final UILanguageAnalyticsEvent event) {
    return new HashMap<String, Object>() {{
      put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, CleverTapEventConstants.UI_LANGUAGE_EVENT);
      put(CleverTapEventConstants.UI_LANGUAGE_ISO3_ATTRIBUTE, event.getLangCode());
      put(CleverTapEventConstants.UI_LANGUAGE_NAME_ATTRIBUTE, event.getLang());
      put(CleverTapEventConstants.USER_ID, preferences.getString("userId", "-1"));
      put(CleverTapEventConstants.DEVICE_ID, preferences.getString("deviceId", "-1"));
      put(CleverTapEventConstants.COUNTRY, event.getCountry());
      put(CleverTapEventConstants.DEVICE_MANUFACTURER, Build.MANUFACTURER);
      put(CleverTapEventConstants.DEVICE_MODEL, Build.MODEL);
      put(CleverTapEventConstants.OS, Build.VERSION.RELEASE);

    }};
  }
}
