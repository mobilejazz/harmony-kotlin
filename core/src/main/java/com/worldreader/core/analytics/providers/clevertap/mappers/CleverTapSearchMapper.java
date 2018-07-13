package com.worldreader.core.analytics.providers.clevertap.mappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.search.SearchAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;

import java.util.*;

public class CleverTapSearchMapper implements CleverTapAnalyticsMapper<SearchAnalyticsEvent> {

  private final SharedPreferences preferences;

  public CleverTapSearchMapper(Context context) {
    preferences = context.getSharedPreferences("wr-preferences", Context.MODE_PRIVATE);
  }

  @Override public Map<String, Object> transform(final SearchAnalyticsEvent event) {

    return new HashMap<String, Object>() {{
      put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, AnalyticsEventConstants.SEARCH_EVENT);
      put(AnalyticsEventConstants.SEARCH_QUERY_ATTRIBUTE, event.getQuery());
      put(AnalyticsEventConstants.SEARCH_LANG_ATTRIBUTE, TextUtils.join(",", event.getLanguages()));
      put(AnalyticsEventConstants.SEARCH_CATEGORY_ATTRIBUTE, TextUtils.join(",", event.getCategories()));
      put(AnalyticsEventConstants.SEARCH_AGE_ATTRIBUTE, TextUtils.join(",", event.getAges()));
      put(CleverTapEventConstants.USER_ID, preferences.getString("userId", "-1"));
      put(CleverTapEventConstants.DEVICE_ID, preferences.getString("deviceId", "-1"));
      put(CleverTapEventConstants.COUNTRY, event.getCountry());
      put(CleverTapEventConstants.DEVICE_MANUFACTURER, Build.MANUFACTURER);
      put(CleverTapEventConstants.DEVICE_MODEL, Build.MODEL);
      put(CleverTapEventConstants.OS, Build.VERSION.RELEASE);
    }};
  }

}
