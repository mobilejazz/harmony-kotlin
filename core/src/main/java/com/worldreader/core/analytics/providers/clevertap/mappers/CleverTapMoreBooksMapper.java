package com.worldreader.core.analytics.providers.clevertap.mappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.worldreader.core.analytics.event.other.MoreBooksAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;

import java.util.*;

public class CleverTapMoreBooksMapper implements CleverTapAnalyticsMapper<MoreBooksAnalyticsEvent> {

  private final SharedPreferences preferences;

  public CleverTapMoreBooksMapper(Context context) {
    preferences = context.getSharedPreferences("wr-preferences", Context.MODE_PRIVATE);
  }

  @Override public Map<String, Object> transform(final MoreBooksAnalyticsEvent event) {
    return new HashMap<String, Object>() {
      {
        put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, CleverTapEventConstants.MORE_BOOKS_EVENT);
        put(CleverTapEventConstants.SHELVE_ATTRIBUTE, event.getShelveId());
        put(CleverTapEventConstants.SHELVE_TITLE_ATTRIBUTE, event.getShelveTitle());
        put(CleverTapEventConstants.USER_ID, preferences.getString("userId", "-1"));
        put(CleverTapEventConstants.DEVICE_ID, preferences.getString("deviceId", "-1"));
        put(CleverTapEventConstants.COUNTRY, event.getCountry());
        put(CleverTapEventConstants.DEVICE_MANUFACTURER, Build.MANUFACTURER);
        put(CleverTapEventConstants.DEVICE_MODEL, Build.MODEL);
        put(CleverTapEventConstants.OS, Build.VERSION.RELEASE);
      }};
  }
}
