package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.register.CampaignAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;
import com.worldreader.core.domain.model.Referrer;

public class PinpointCampaignMapper implements PinpointAnalyticsMapper<CampaignAnalyticsEvent> {

  private final AnalyticsClient ac;

  public PinpointCampaignMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(CampaignAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.CAMPAIGN_ANALYTICS_EVENT);
    analyticsEvent.addAttribute(Referrer.KEY_UTM_SOURCE, event.getUtmSource());
    analyticsEvent.addAttribute(Referrer.KEY_UTM_MEDIUM, event.getUtmMedium());
    analyticsEvent.addAttribute(Referrer.KEY_UTM_TERM, event.getUtmTerm());
    analyticsEvent.addAttribute(Referrer.KEY_UTM_CONTENT, event.getUtmContent());
    analyticsEvent.addAttribute(Referrer.KEY_UTM_CAMPAIGN, event.getUtmCampaign());
    analyticsEvent.addAttribute(Referrer.KEY_ANID_CAMPAIGN, event.getAnId());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.REFERRER_DEVICE_ID_ATTRIBUTE, event.getReferrerDeviceId());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.REFERRER_USER_ID_ATTRIBUTE, event.getReferrerUserId());

    return analyticsEvent;

  }
}
