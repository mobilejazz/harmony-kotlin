package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.books.BookDetailAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointBookDetailsMapper implements PinpointAnalyticsMapper<BookDetailAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointBookDetailsMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(BookDetailAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(AnalyticsEventConstants.BOOK_READ_EVENT);
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.BOOK_ID_ATTRIBUTE, event.getId());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.BOOK_TITLE_ATTRIBUTE, event.getTitle());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.BOOK_VERSION_ATTRIBUTE, PinpointMobileAnalyticsConstants.getBookVersionIntValue(event
        .getVersion()));
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.REFERRING_SCREEN, event.getReferringScreen());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.COLLECTION_ID_ATTRIBUTE, event.getCollectionId());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.COLLECTION_TITLE_ATTRIBUTE, event.getCollectionName());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.SHELVE_ATTRIBUTE, event.getShelveId());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.SHELVE_TITLE_ATTRIBUTE, event.getShelveTitle());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.REFERRING_SCREEN, event.getReferringScreen());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.REFERRING_META, event.getReferringMeta());

    return analyticsEvent;
  }


}
