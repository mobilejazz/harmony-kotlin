package com.worldreader.core.analytics.providers.clevertap.mappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.worldreader.core.analytics.event.categories.CategoryBooksAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;

import java.util.*;

public class CleverTapCategoryBooksMapper implements CleverTapAnalyticsMapper<CategoryBooksAnalyticsEvent> {
  private final SharedPreferences preferences;

  public CleverTapCategoryBooksMapper(Context context) {
    preferences = context.getSharedPreferences("wr-preferences", Context.MODE_PRIVATE);
  }

  @Override public Map<String, Object> transform(final CategoryBooksAnalyticsEvent event) {
    return new HashMap<String, Object>() {{

      put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, CleverTapEventConstants.CATEGORY_BOOKS_EVENT);
      put(CleverTapEventConstants.CATEGORY_NAME_ATTRIBUTE, event.getCategoryScreen());
      //put(CleverTapEventConstants.SHELVE_ATTRIBUTE, event.getShelveId());
      //put(CleverTapEventConstants.SHELVE_TITLE_ATTRIBUTE, event.getShelveName());

      put(CleverTapEventConstants.USER_ID, preferences.getString("userId", "-1"));
      put(CleverTapEventConstants.DEVICE_ID, preferences.getString("deviceId", "-1"));
      put(CleverTapEventConstants.COUNTRY, "");//TODO
      put(CleverTapEventConstants.DEVICE_MANUFACTURER, Build.MANUFACTURER);
      put(CleverTapEventConstants.DEVICE_MODEL, Build.MODEL);
      put(CleverTapEventConstants.OS, Build.VERSION.RELEASE);
    }};
  }


}
