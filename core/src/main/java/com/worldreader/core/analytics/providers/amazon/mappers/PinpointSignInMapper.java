package com.worldreader.core.analytics.providers.amazon.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.register.SignInAnalyticsEvent;
import com.worldreader.core.analytics.providers.amazon.PinpointMobileAnalyticsConstants;

public class PinpointSignInMapper implements PinpointAnalyticsMapper<SignInAnalyticsEvent> {

  private final AnalyticsClient ac;

  public PinpointSignInMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(SignInAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.LOGIN_EVENT);
    return analyticsEvent;
  }
}
