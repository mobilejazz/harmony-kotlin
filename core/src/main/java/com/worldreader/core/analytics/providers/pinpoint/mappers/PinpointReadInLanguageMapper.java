package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.other.ChangeLanguageAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointReadInLanguageMapper implements PinpointAnalyticsMapper<ChangeLanguageAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointReadInLanguageMapper(AnalyticsClient ac) {
    this.ac = ac;
  }


  @Override public AnalyticsEvent transform(ChangeLanguageAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.READ_IN_LANGUAGE_EVENT);

    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.LANGUAGE_ISO3_ATTRIBUTE, event.getLangCode());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.LANGUAGE_NAME_ATTRIBUTE, event.getLang());
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRY, event.getCountry());

    return analyticsEvent;
  }

}
