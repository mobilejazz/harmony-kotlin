package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.register.SignInAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointSignInMapper implements PinpointAnalyticsMapper<SignInAnalyticsEvent> {

  private final AnalyticsClient ac;

  public PinpointSignInMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(SignInAnalyticsEvent event) {
    // TODO: 21/05/2018 To be written
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.LOGIN_EVENT);
    return analyticsEvent;
  }
}
