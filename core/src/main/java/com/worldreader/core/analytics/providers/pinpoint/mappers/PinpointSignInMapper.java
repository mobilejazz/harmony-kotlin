package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.register.SignInAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointSignInMapper implements PinpointAnalyticsMapper<SignInAnalyticsEvent> {

  private final AnalyticsClient ac;

  public PinpointSignInMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(SignInAnalyticsEvent event) {

    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.LOGIN_EVENT);
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.REGISTER_ATTRIBUTE, event.getRegister());
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRTY_CODE, event.getCountry());

    return analyticsEvent;
  }
}
