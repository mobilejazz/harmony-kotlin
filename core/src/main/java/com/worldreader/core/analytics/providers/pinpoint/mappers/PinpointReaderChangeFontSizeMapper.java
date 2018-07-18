package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.reader.ReaderChangeFontSizeAnalyticsEvent;

public class PinpointReaderChangeFontSizeMapper implements PinpointAnalyticsMapper<ReaderChangeFontSizeAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointReaderChangeFontSizeMapper(AnalyticsClient ac) {
    this.ac = ac;
  }


  @Override public AnalyticsEvent transform(ReaderChangeFontSizeAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(AnalyticsEventConstants.CHANGE_FONT_SIZE_EVENT);

    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_ID_ATTRIBUTE, event.getBookId());
    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_TITLE_ATTRIBUTE, event.getBookTitle());
    analyticsEvent.addAttribute(AnalyticsEventConstants.SELECTED_FONT_SIZE, event.getNewFontSize());
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRY, event.getCountry());

    return analyticsEvent;
  }

}
