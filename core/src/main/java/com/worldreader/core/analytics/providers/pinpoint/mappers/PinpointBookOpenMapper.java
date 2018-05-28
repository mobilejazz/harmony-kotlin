package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.books.BookOpenAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointBookOpenMapper implements PinpointAnalyticsMapper<BookOpenAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointBookOpenMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(BookOpenAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.BOOK_OPEN_EVENT);
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.BOOK_ID_ATTRIBUTE, event.getId());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.BOOK_TITLE_ATTRIBUTE, event.getTitle());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.BOOK_VERSION_ATTRIBUTE, PinpointMobileAnalyticsConstants.getBookVersionIntValue(event
        .getVersion()));
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.CATEGORY_ID_ATTRIBUTE, event.getCategoryId());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.CATEGORY_TITLE_ATTRIBUTE, event.getCategory());

    return analyticsEvent;
  }
}
