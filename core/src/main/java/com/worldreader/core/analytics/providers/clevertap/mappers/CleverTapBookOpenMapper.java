package com.worldreader.core.analytics.providers.clevertap.mappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.worldreader.core.analytics.event.books.BookOpenAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;

import java.util.*;

public class CleverTapBookOpenMapper implements CleverTapAnalyticsMapper<BookOpenAnalyticsEvent> {

  private final SharedPreferences preferences;

  public CleverTapBookOpenMapper(Context context) {
    preferences = context.getSharedPreferences("wr-preferences", Context.MODE_PRIVATE);
  }

  @Override public Map<String, Object> transform(final BookOpenAnalyticsEvent event) {
    return new HashMap<String, Object>() {{
      put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, CleverTapEventConstants.BOOK_OPEN_EVENT);
      put(CleverTapEventConstants.BOOK_ID, event.getId());
      put(CleverTapEventConstants.BOOK_TITLE, event.getTitle());
      put(CleverTapEventConstants.BOOK_VERSION, event.getVersion());
      put(CleverTapEventConstants.BOOK_PUBLISHER, event.getPublisher());
      put(CleverTapEventConstants.BOOK_CATEGORY, event.getCategory());
      put(CleverTapEventConstants.BOOK_CATEGORY_ID, event.getCategoryId());
      put(CleverTapEventConstants.IS_READING_OFFLINE, event.getVariant());
      put(CleverTapEventConstants.COUNTRY, event.getCountry());
      put(CleverTapEventConstants.USER_ID, preferences.getString("userId", "-1"));
      put(CleverTapEventConstants.DEVICE_ID, preferences.getString("deviceId", "-1"));
      put(CleverTapEventConstants.DEVICE_MANUFACTURER, Build.MANUFACTURER);
      put(CleverTapEventConstants.DEVICE_MODEL, Build.MODEL);
      put(CleverTapEventConstants.OS, Build.VERSION.RELEASE);

    }};
  }
}
