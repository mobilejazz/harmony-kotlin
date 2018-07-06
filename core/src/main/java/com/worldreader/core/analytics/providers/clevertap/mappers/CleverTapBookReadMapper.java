package com.worldreader.core.analytics.providers.clevertap.mappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.books.BookReadAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;

import java.util.*;

public class CleverTapBookReadMapper implements CleverTapAnalyticsMapper<BookReadAnalyticsEvent> {

  private final SharedPreferences preferences;

  public CleverTapBookReadMapper(Context context) {
    preferences = context.getSharedPreferences("wr-preferences", Context.MODE_PRIVATE);
  }


  @Override public Map<String, Object> transform(final BookReadAnalyticsEvent event) {
    return new HashMap<String, Object>() {{
      put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, CleverTapEventConstants.BOOK_READ_EVENT);
      put(CleverTapEventConstants.BOOK_ID, event.getId());
      put(CleverTapEventConstants.BOOK_TITLE, event.getTitle());
      put(CleverTapEventConstants.BOOK_VERSION, event.getVersion());
      put(CleverTapEventConstants.BOOK_PUBLISHER, event.getPublisher());
      put(CleverTapEventConstants.BOOK_CATEGORY, event.getCategory());
      put(CleverTapEventConstants.BOOK_CATEGORY_ID, event.getCategoryId());
      put(CleverTapEventConstants.IS_READING_OFFLINE, event.getVariant());
      put(CleverTapEventConstants.BOOK_PROGRESS, "");//TODO
      put(AnalyticsEventConstants.BOOK_READING_SPINE_ELEM_IN_SPINE_POSITION, String.valueOf(event.getSpinePosition()));
      put(AnalyticsEventConstants.BOOK_READING_SPINE_ELEM_SIZE_IN_CHARS, String.valueOf(event.getText().length()));
      put(AnalyticsEventConstants.BOOK_READING_CURRENT_PAGE_IN_SPINE_ELEM, String.valueOf(event.getCurrentPage()));
      put(AnalyticsEventConstants.BOOK_READING_AMOUNT_OF_PAGES_IN_SPINE_ELEM, String.valueOf(event.getPagesForResouce()));
      put(AnalyticsEventConstants.BOOK_READING_SCREEN_TEXT_SIZE_IN_CHARS, String.valueOf(event.getTextSizeInChars()));
      put(CleverTapEventConstants.COUNTRY, event.getCountry());
      put(CleverTapEventConstants.USER_ID, preferences.getString("userId", "-1"));
      put(CleverTapEventConstants.DEVICE_ID, preferences.getString("deviceId", "-1"));
      put(CleverTapEventConstants.DEVICE_MANUFACTURER, Build.MANUFACTURER);
      put(CleverTapEventConstants.DEVICE_MODEL, Build.MODEL);
      put(CleverTapEventConstants.OS, Build.VERSION.RELEASE);

    }};
  }
}
