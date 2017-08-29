package com.worldreader.core.analytics.event;

public class AnalyticsEventConstants {

  public static final String BOOK_READ_EVENT = "BookPageRead";
  public static final String BOOK_ID_ATTRIBUTE = "bookId";
  public static final String BOOK_TITLE_ATTRIBUTE = "bookTitle";
  public static final String APP_IN_OFFLINE = "offlineMode";
  public static final String BOOK_AMOUNT_OF_TOC_ENTRIES = "bookAmountOfTocEntries";
  public static final String BOOK_SPINE_SIZE = "bookSpineSize";
  public static final String BOOK_OPEN_TOC_EVENT = "BookOpenToc";
  public static final String TOC_ENTRY_TITLE_ATTRIBUTE = "tocEntryTitle";
  public static final String TOC_ENTRY_SELECTED_EVENT = "GoToTocEntry";

  public static final String BOOK_READING_SPINE_ELEM_IN_SPINE_POSITION = "bookCurrentlyReadingSpineElement";
  public static final String BOOK_READING_SPINE_ELEM_SIZE_IN_CHARS = "spineElementSizeInChars";
  public static final String BOOK_READING_SCREEN_TEXT_SIZE_IN_CHARS = "screenTextSizeInChars";
  public static final String BOOK_READING_SCREEN_AMOUNT_OF_IMAGES = "screenAmountOfImages";
  public static final String BOOK_READING_CURRENT_PAGE_IN_SPINE_ELEM = "currentPageNumberInSpineElement";
  public static final String BOOK_READING_AMOUNT_OF_PAGES_IN_SPINE_ELEM = "amountOfPagesForSpineElement";


  public static class Reader {
    public static final String OPEN_READER = "Open";
  }

}
