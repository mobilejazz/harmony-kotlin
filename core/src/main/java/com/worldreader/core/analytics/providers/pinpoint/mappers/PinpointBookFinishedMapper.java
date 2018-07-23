package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.books.BookFinishedAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointBookFinishedMapper implements PinpointAnalyticsMapper<BookFinishedAnalyticsEvent> {

  private final AnalyticsClient ac;

  public PinpointBookFinishedMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(BookFinishedAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.BOOK_FINISHED_EVENT);
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.BOOK_ID_ATTRIBUTE, event.getId());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.BOOK_TITLE_ATTRIBUTE, event.getTitle());
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRTY_CODE, event.getCountry());

    return analyticsEvent;
  }
}
