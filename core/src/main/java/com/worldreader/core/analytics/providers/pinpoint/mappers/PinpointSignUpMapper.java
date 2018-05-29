package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.register.SignUpAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointSignUpMapper implements PinpointAnalyticsMapper<SignUpAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointSignUpMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(SignUpAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.SIGNUP_EVENT);
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.REGISTER_ATTRIBUTE, event.getRegister());

    return analyticsEvent;

  }
}
