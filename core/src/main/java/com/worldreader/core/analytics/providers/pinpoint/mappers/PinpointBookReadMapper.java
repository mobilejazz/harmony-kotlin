package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.books.BookReadAnalyticsEvent;

public class PinpointBookReadMapper implements PinpointAnalyticsMapper<BookReadAnalyticsEvent> {

  private final AnalyticsClient ac;

  public PinpointBookReadMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(BookReadAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(AnalyticsEventConstants.BOOK_READ_EVENT);
    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_ID_ATTRIBUTE, event.getId());
    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_TITLE_ATTRIBUTE, event.getTitle());
    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_AMOUNT_OF_TOC_ENTRIES, String.valueOf(event.getTocSize()));
    analyticsEvent.addAttribute(AnalyticsEventConstants. BOOK_SPINE_SIZE, String.valueOf(event.getSpineSize()));
    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_READING_SPINE_ELEM_IN_SPINE_POSITION, String.valueOf(event.getSpinePosition()));
    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_READING_SPINE_ELEM_SIZE_IN_CHARS, String.valueOf(event.getText().length()));
    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_READING_CURRENT_PAGE_IN_SPINE_ELEM, String.valueOf(event.getCurrentPage()));
    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_READING_AMOUNT_OF_PAGES_IN_SPINE_ELEM, String.valueOf(event.getPagesForResouce()));
    analyticsEvent.addAttribute(AnalyticsEventConstants.BOOK_READING_SCREEN_TEXT_SIZE_IN_CHARS, String.valueOf(event.getTextSizeInChars()));

    return analyticsEvent;
  }
}
