package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.other.ScreenNameAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointInScreenMapper implements PinpointAnalyticsMapper<ScreenNameAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointInScreenMapper(AnalyticsClient ac) {
    this.ac = ac;
  }


  @Override public AnalyticsEvent transform(ScreenNameAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.IN_SCREEN);
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.SCREEN_NAME_ATTRIBUTE, event.getScreenName());
    analyticsEvent.addAttribute(AnalyticsEventConstants.APP_IN_OFFLINE, "");
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRTY_CODE, event.getCountryCode());
    return analyticsEvent;
  }

}
