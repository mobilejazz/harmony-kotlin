package com.worldreader.core.analytics.providers.clevertap.mappers;

import com.worldreader.core.analytics.event.books.BookDetailAnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;
import java.util.HashMap;
import java.util.Map;

public class CleverTapBookDetailMapper implements CleverTapAnalyticsMapper<BookDetailAnalyticsEvent> {

  @Override public Map<String, Object> transform(final BookDetailAnalyticsEvent event) {
    return new HashMap<String, Object>() {{
      put(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME, "BookDetails");
      put("BookId", event.getId());
      put("BookVersion", event.getVersion());
      put("BookPublisher", event.getPublisher());
      put("BookAuthor", event.getAuthor());
      put("BookCategory", event.getCategory());
      put("BookCategoryId", event.getCategoryId());
      put("Os", "Android");
      put("Country", "");
      put("UserId", "");
    }};
  }
}
