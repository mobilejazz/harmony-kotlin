package com.worldreader.core.analytics.providers.clevertap.mappers;

import android.os.Build;
import com.worldreader.core.analytics.event.books.BookContinueReadingAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;

import java.util.HashMap;
import java.util.Map;

import static com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants.BOOK_CONTINUE_READING_EVENT;

public class CleverTapBookContinueReadingMapper implements CleverTapAnalyticsMapper<BookContinueReadingAnalyticsEvent> {

  @Override public Map<String, Object> transform(final BookContinueReadingAnalyticsEvent event) {
    return new HashMap<String, Object>() {{
      put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, CleverTapEventConstants.BOOK_CONTINUE_READING_EVENT);
      put(CleverTapEventConstants.BOOK_ID, event.getId());
      put(CleverTapEventConstants.BOOK_TITLE, event.getTitle());
      put(CleverTapEventConstants.BOOK_VERSION, event.getVersion());
      put(CleverTapEventConstants.BOOK_PUBLISHER, event.getPublisher());
      put(CleverTapEventConstants.BOOK_CATEGORY, event.getCategory());
      put(CleverTapEventConstants.BOOK_CATEGORY_ID, event.getCategoryId());
      put(CleverTapEventConstants.IS_READING_OFFLINE, event.getVariant());
      put(CleverTapEventConstants.COUNTRY, "_TODO_");
      put(CleverTapEventConstants.USER_ID, "_TODO_");
      put(CleverTapEventConstants.DEVICE_ID, "_TODO_");
      put(CleverTapEventConstants.DEVICE_MANUFACTURER, Build.MANUFACTURER);
      put(CleverTapEventConstants.DEVICE_MODEL, Build.MODEL);
      put(CleverTapEventConstants.OS, Build.VERSION.RELEASE);

    }};
  }
}
