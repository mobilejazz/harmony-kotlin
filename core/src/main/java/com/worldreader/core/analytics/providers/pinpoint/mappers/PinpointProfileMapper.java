package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.register.ProfileAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointProfileMapper implements PinpointAnalyticsMapper<ProfileAnalyticsEvent> {
  private final AnalyticsClient ac;



  public PinpointProfileMapper(AnalyticsClient ac) {
    this.ac = ac;
  }


  @Override public AnalyticsEvent transform(ProfileAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.READ_IN_LANGUAGE_EVENT);

    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.LANGUAGE_ISO3_ATTRIBUTE, event.getLanguage());
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRY, event.getCountry());


    return analyticsEvent;
  }

}
