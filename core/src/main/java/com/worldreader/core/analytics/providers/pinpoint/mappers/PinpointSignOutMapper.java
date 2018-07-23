package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.register.SignOutAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointSignOutMapper implements PinpointAnalyticsMapper<SignOutAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointSignOutMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(SignOutAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.SIGNOUT_EVENT);
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRTY_CODE, event.getCountry());

    return analyticsEvent;

  }
}
