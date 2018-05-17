package com.worldreader.core.analytics.providers.clevertap.mappers;

import com.worldreader.core.analytics.event.books.BookDetailAnalyticsEvent;
import java.util.HashMap;
import java.util.Map;

public class CleverTapBookDetailMapper implements CleverTapAnalyticsMapper<BookDetailAnalyticsEvent> {

  @Override public Map<String, Object> transform(final BookDetailAnalyticsEvent event) {
    return new HashMap<String, Object>() {{
      put("BookId", event.getId());
      put("BookVersion", event.getVersion());
      put("BookPublisher", event.getPublisher());
      put("BookAuthor", event.getAuthor());
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
