package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.other.MoreBooksAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointMoreBooksMapper implements PinpointAnalyticsMapper<MoreBooksAnalyticsEvent> {

  private final AnalyticsClient ac;

  public PinpointMoreBooksMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(MoreBooksAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.MORE_BOOKS_EVENT);
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.SHELVE_ATTRIBUTE, event.getShelveId());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.SHELVE_TITLE_ATTRIBUTE, event.getShelveTitle());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.CATEGORY_ID_ATTRIBUTE, event.getCategoryId());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.CATEGORY_TITLE_ATTRIBUTE, event.getCategoryTitle());
    return analyticsEvent;
  }
}
