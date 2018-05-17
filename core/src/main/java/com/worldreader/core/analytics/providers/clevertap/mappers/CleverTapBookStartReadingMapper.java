package com.worldreader.core.analytics.providers.clevertap.mappers;

import com.worldreader.core.analytics.event.books.BookStartReadingAnalyticsEvent;
import java.util.HashMap;
import java.util.Map;

public class CleverTapBookStartReadingMapper implements CleverTapAnalyticsMapper<BookStartReadingAnalyticsEvent> {

  @Override public Map<String, Object> transform(final BookStartReadingAnalyticsEvent event) {
    return new HashMap<String, Object>() {{
      put("BookId", event.getId());
      put("BookTitle", event.getTitle());
      put("BookVersion", event.getVersion());
      put("BookPublisher", event.getPublisher());
      put("BookCategory", event.getCategory());
      put("BookCategoryId", event.getCategoryId());

      // TODO: 16/05/2018 We should remove this params (just giving default values to be aware of them)
      put("Host", "");
      put("Url", "");
      put("ClientId", "");
      put("BrowserId", "");
      put("UserId", "");
      put("IpAddress", "");
      put("ForwaredFor", "");
      put("Country", "");
      put("UserAgent", "");
      put("Browser", "");
      put("Os", "Android");
    }};
  }
}
