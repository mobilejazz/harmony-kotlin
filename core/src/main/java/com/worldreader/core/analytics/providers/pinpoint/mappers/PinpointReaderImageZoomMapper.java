package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.reader.ReaderImageZoomAnalyticsEvent;

public class PinpointReaderImageZoomMapper implements PinpointAnalyticsMapper<ReaderImageZoomAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointReaderImageZoomMapper(AnalyticsClient ac) {
    this.ac = ac;
  }


  @Override public AnalyticsEvent transform(ReaderImageZoomAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(AnalyticsEventConstants.IMAGE_ZOOM_EVENT);

    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_ID_ATTRIBUTE, event.getBookId());
    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_TITLE_ATTRIBUTE, event.getBookTitle());
    analyticsEvent.addAttribute(AnalyticsEventConstants.SELECTED_IMAGE, event.getImageName());
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRY, event.getCountry());

    return analyticsEvent;
  }

}
