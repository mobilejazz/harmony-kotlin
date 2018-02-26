package com.worldreader.core.analytics.reader;

import com.worldreader.core.analytics.Analytics;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.BasicAnalyticsEvent;

import java.util.*;

// Wrapper class to perform reader analytics
public class ReaderAnalytics {

  public static void sendFormattedChapterEvent(Analytics analytics, final String bookId, final String title, final int pagesForResource, final int currentPage,
      final CharSequence text, final int tocSize, final int spineSize, final int spinePosition, final int textSizeInChars) {
    if (pagesForResource > 0) {
      final Map<String, String> attrs = new HashMap<String, String>() {{
        //Book toc size
        put(AnalyticsEventConstants.BOOK_AMOUNT_OF_TOC_ENTRIES, String.valueOf(tocSize));

        // Book spine size
        put(AnalyticsEventConstants. BOOK_SPINE_SIZE, String.valueOf(spineSize));

        //Currently reading toc entry number
        put(AnalyticsEventConstants.BOOK_READING_SPINE_ELEM_IN_SPINE_POSITION, String.valueOf(spinePosition));
        put(AnalyticsEventConstants.BOOK_READING_SPINE_ELEM_SIZE_IN_CHARS, String.valueOf(text.length()));
        put(AnalyticsEventConstants.BOOK_READING_CURRENT_PAGE_IN_SPINE_ELEM, String.valueOf(currentPage));
        put(AnalyticsEventConstants.BOOK_READING_AMOUNT_OF_PAGES_IN_SPINE_ELEM, String.valueOf(pagesForResource));
        put(AnalyticsEventConstants.BOOK_READING_SCREEN_TEXT_SIZE_IN_CHARS, String.valueOf(textSizeInChars));
        put(AnalyticsEventConstants.BOOK_ID_ATTRIBUTE, bookId);
        put(AnalyticsEventConstants.BOOK_TITLE_ATTRIBUTE, title);
      }};

      analytics.sendEvent(new BasicAnalyticsEvent(AnalyticsEventConstants.BOOK_READ_EVENT, attrs));
    }
  }


  public static void sendOpenTocEvent(Analytics analytics, final String bookId, final String title){
    final Map<String, String> attrs = new HashMap<String, String>() {{
      put(AnalyticsEventConstants.BOOK_ID_ATTRIBUTE, bookId);
      put(AnalyticsEventConstants.BOOK_TITLE_ATTRIBUTE, title);
    }};

    analytics.sendEvent(new BasicAnalyticsEvent(AnalyticsEventConstants.BOOK_OPEN_TOC_EVENT, attrs));

  }

  public static void sendOpenTocEntryEvent(Analytics analytics, final String bookId, final String title, final String tocEntryTitle, final String tocEntryHref){
    final Map<String, String> attrs = new HashMap<String, String>() {{
      put(AnalyticsEventConstants.BOOK_ID_ATTRIBUTE, bookId);
      put(AnalyticsEventConstants.BOOK_TITLE_ATTRIBUTE, title);
      put(AnalyticsEventConstants.TOC_ENTRY_TITLE_ATTRIBUTE, tocEntryTitle);
      put(AnalyticsEventConstants.TOC_ENTRY_HREF, tocEntryHref);

    }};

    analytics.sendEvent(new BasicAnalyticsEvent(AnalyticsEventConstants.TOC_ENTRY_SELECTED_EVENT, attrs));
  }

}
