package com.worldreader.core.analytics.providers.clevertap.mappers;

import com.worldreader.core.analytics.event.books.BookReadAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;
import java.util.HashMap;
import java.util.Map;

public class CleverTapBookReadMapper implements CleverTapAnalyticsMapper<BookReadAnalyticsEvent> {

  @Override public Map<String, Object> transform(final BookReadAnalyticsEvent event) {
    return new HashMap<String, Object>() {{
      put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, "BookEnd");
      put("BookId", event.getId());
      put("BookTitle", event.getTitle());
      put("BookVersion", event.getVersion());
      put("BookPublisher", event.getPublisher());
      put("BookCategory", event.getCategory());
      put("BookCategoryId", event.getCategoryId());
      put("Os", "Android");
      put("Country", "");
      put("UserId", "");
    }};
  }
}
