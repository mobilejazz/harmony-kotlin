package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.categories.CategorySelectedAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.PinpointMobileAnalyticsConstants;

class PinpointCategoryDetailsMapper implements PinpointAnalyticsMapper<CategorySelectedAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointCategoryDetailsMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(final CategorySelectedAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(PinpointMobileAnalyticsConstants.CATEGORY_SELECTION_EVENT);
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.CATEGORY_ID_ATTRIBUTE, String.valueOf(event.getCategoryId()));
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.CATEGORY_TITLE_ATTRIBUTE, event.getCategoryName());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.PARENT_CATEGORY_ID_ATTRIBUTE,event.getParentCategoryId());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.PARENT_CATEGORY_TITLE_ATTRIBUTE, event.getParentCategoryName());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.REFERRING_SCREEN, event.getReferringScreen());
    analyticsEvent.addAttribute(PinpointMobileAnalyticsConstants.REFERRING_META, event.getReferringMeta());

    return analyticsEvent;

  }

}
