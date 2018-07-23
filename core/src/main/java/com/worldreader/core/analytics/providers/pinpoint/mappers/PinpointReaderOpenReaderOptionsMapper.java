package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.reader.ReaderOpenReaderOptionsAnalyticsEvent;

public class PinpointReaderOpenReaderOptionsMapper implements PinpointAnalyticsMapper<ReaderOpenReaderOptionsAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointReaderOpenReaderOptionsMapper(AnalyticsClient ac) {
    this.ac = ac;
  }


  @Override public AnalyticsEvent transform(ReaderOpenReaderOptionsAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(AnalyticsEventConstants.OPEN_READER_OPTIONS);

    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_ID_ATTRIBUTE, event.getBookId());
    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_TITLE_ATTRIBUTE, event.getBookTitle());
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRTY_CODE, event.getCountry());

    return analyticsEvent;
  }

}
