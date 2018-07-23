package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.other.UILanguageAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointUILanguageMapper implements PinpointAnalyticsMapper<UILanguageAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointUILanguageMapper(AnalyticsClient ac) {
    this.ac = ac;
  }


  @Override public AnalyticsEvent transform(UILanguageAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.UI_LANGUAGE_EVENT);

    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.UI_LANGUAGE_ISO3_ATTRIBUTE, event.getLangCode());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.UI_LANGUAGE_NAME_ATTRIBUTE, event.getLang());
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRTY_CODE, event.getCountry());

    return analyticsEvent;
  }

}
