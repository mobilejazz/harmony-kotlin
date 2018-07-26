package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.register.AcceptPrivacyAnalyticEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointAcceptPrivacyMapper implements PinpointAnalyticsMapper<AcceptPrivacyAnalyticEvent> {

  private final AnalyticsClient ac;

  public PinpointAcceptPrivacyMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(AcceptPrivacyAnalyticEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.ACCEPTS_PRIVACY);

    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRTY_CODE, event.getCountry());

    return analyticsEvent;

  }
}
