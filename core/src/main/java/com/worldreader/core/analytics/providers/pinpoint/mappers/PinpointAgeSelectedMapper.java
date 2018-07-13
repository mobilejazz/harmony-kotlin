package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.other.AgeSelectedAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointAgeSelectedMapper implements PinpointAnalyticsMapper<AgeSelectedAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointAgeSelectedMapper(AnalyticsClient ac) {
    this.ac = ac;
  }


  @Override public AnalyticsEvent transform(AgeSelectedAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.AGE_SELECTED_EVENT);

    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.AGE_SELECTED_ATTRIBUTE, event.getAgeSelected());
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRY, event.getCountry());

    return analyticsEvent;
  }

}
