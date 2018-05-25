package com.worldreader.core.analytics.providers.clevertap.mappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.worldreader.core.analytics.event.categories.CategorySelectedAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;

import java.util.*;

public class CleverTapCategoryDetailsMapper implements CleverTapAnalyticsMapper<CategorySelectedAnalyticsEvent> {

  private final SharedPreferences preferences;

  public CleverTapCategoryDetailsMapper(Context context) {
    preferences = context.getSharedPreferences("wr-analytics", Context.MODE_PRIVATE);
  }

  @Override public Map<String, Object> transform(final CategorySelectedAnalyticsEvent event) {
    return new HashMap<String, Object>() {{

      put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, CleverTapEventConstants.CATEGORY_DETAILS_EVENT);
      put(CleverTapEventConstants.CATEGORY_ID_ATTRIBUTE, event.getCategoryId());
      put(CleverTapEventConstants.CATEGORY_NAME_ATTRIBUTE, event.getCategoryName());
      put(CleverTapEventConstants.USER_ID, preferences.getString("userId", "-1"));
      put(CleverTapEventConstants.DEVICE_ID, preferences.getString("deviceId", "-1"));
      put(CleverTapEventConstants.COUNTRY, "");//TODO
      put(CleverTapEventConstants.DEVICE_MANUFACTURER, Build.MANUFACTURER);
      put(CleverTapEventConstants.DEVICE_MODEL, Build.MODEL);
      put(CleverTapEventConstants.OS, Build.VERSION.RELEASE);
    }};
  }
}
