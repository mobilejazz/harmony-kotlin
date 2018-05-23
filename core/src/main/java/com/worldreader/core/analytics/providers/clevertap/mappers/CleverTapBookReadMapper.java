package com.worldreader.core.analytics.providers.clevertap.mappers;

import android.os.Build;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.books.BookReadAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;
import java.util.HashMap;
import java.util.Map;

public class CleverTapBookReadMapper implements CleverTapAnalyticsMapper<BookReadAnalyticsEvent> {

  @Override public Map<String, Object> transform(final BookReadAnalyticsEvent event) {
    return new HashMap<String, Object>() {{
      put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, "BookRead");
      put(CleverTapEventConstants.BOOK_ID, event.getId());
      put(CleverTapEventConstants.BOOK_TITLE, event.getTitle());
      put(CleverTapEventConstants.BOOK_VERSION, event.getVersion());
      put(CleverTapEventConstants.BOOK_PUBLISHER, event.getPublisher());
      put(CleverTapEventConstants.BOOK_CATEGORY, event.getCategory());
      put(CleverTapEventConstants.BOOK_CATEGORY_ID, event.getCategoryId());
      put(CleverTapEventConstants.IS_READING_OFFLINE, event.getVariant());
      put(CleverTapEventConstants.BOOK_PROGRESS, "_TODO_");
      put(AnalyticsEventConstants.BOOK_READING_SPINE_ELEM_IN_SPINE_POSITION, String.valueOf(event.getSpinePosition()));
      put(AnalyticsEventConstants.BOOK_READING_SPINE_ELEM_SIZE_IN_CHARS, String.valueOf(event.getText().length()));
      put(AnalyticsEventConstants.BOOK_READING_CURRENT_PAGE_IN_SPINE_ELEM, String.valueOf(event.getCurrentPage()));
      put(AnalyticsEventConstants.BOOK_READING_AMOUNT_OF_PAGES_IN_SPINE_ELEM, String.valueOf(event.getPagesForResouce()));
      put(AnalyticsEventConstants.BOOK_READING_SCREEN_TEXT_SIZE_IN_CHARS, String.valueOf(event.getTextSizeInChars()));

      put(CleverTapEventConstants.COUNTRY, "_TODO_");
      put(CleverTapEventConstants.USER_ID, "_TODO_");
      put(CleverTapEventConstants.DEVICE_ID, "_TODO_");
      put(CleverTapEventConstants.DEVICE_MANUFACTURER, Build.MANUFACTURER);
      put(CleverTapEventConstants.DEVICE_MODEL, Build.MODEL);
      put(CleverTapEventConstants.OS, Build.VERSION.RELEASE);

    }};
  }
}
