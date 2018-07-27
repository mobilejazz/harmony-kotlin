package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.notifications.NotificationsStatusAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

public class PinpointAnalyticsNotificationStatusMapper implements PinpointAnalyticsMapper<NotificationsStatusAnalyticsEvent> {

  private final AnalyticsClient ac;

  public PinpointAnalyticsNotificationStatusMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(NotificationsStatusAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.NOTIFICATIONS_STATUS);
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.NOTIFICATIONS_ENABLED, event.isEnabled()? "TRUE" : "FALSE");
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRTY_CODE, event.getCountryCode());

    return analyticsEvent;
  }
}
