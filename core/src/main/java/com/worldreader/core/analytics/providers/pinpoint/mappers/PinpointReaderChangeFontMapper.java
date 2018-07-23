package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.reader.ReaderChangeFontTypeAnalyticsEvent;

public class PinpointReaderChangeFontMapper implements PinpointAnalyticsMapper<ReaderChangeFontTypeAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointReaderChangeFontMapper(AnalyticsClient ac) {
    this.ac = ac;
  }


  @Override public AnalyticsEvent transform(ReaderChangeFontTypeAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(AnalyticsEventConstants.CHANGE_FONT_TYPE_EVENT);

    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_ID_ATTRIBUTE, event.getBookId());
    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_TITLE_ATTRIBUTE, event.getBookTitle());
    analyticsEvent.addAttribute(AnalyticsEventConstants.SELECTED_FONT, event.getNewFont());
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRTY_CODE, event.getCountry());

    return analyticsEvent;
  }

}
