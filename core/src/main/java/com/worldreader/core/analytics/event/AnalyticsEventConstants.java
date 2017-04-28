package com.worldreader.core.analytics.event;

public class AnalyticsEventConstants {

  public static final String BOOK_ATTRIBUTE = "BookId";
  public static final String BOOK_TITLE = "BookTitle";
  public static final String BOOK_READ_CONTROLLER = "Book";
  public static final String BOOK_READ_ACTION = "Read";

  public static final String BOOK_AMOUNT_OF_TOC_ENTRIES = "BookAmountOfTocEntries";
  public static final String BOOK_SPINE_SIZE = "BookSpineSize";

  public static final String BOOK_READING_CHAPTER_IN_BOOK = "BookCurrentlyReadingTocEntry";//Chunk number -- Equivalent valued will be chapter
  public static final String BOOK_READING_CHAPTER_SIZE_IN_CHARS = "TocEntrySizeInChars";
  public static final String BOOK_READING_SCREEN_TEXT_SIZE_IN_CHARS = "ScreenTextSizeInChars";
  public static final String BOOK_READING_SCREEN_AMOUNT_OF_IMAGES = "ScreenAmountOfImages";
  public static final String BOOK_READING_CURRENT_PAGE_IN_TOC_ENTRY = "CurrentPageNumberInTocEntry";
  public static final String BOOK_READING_AMOUNT_OF_PAGES_IN_TOC_ENTRY = "AmountOfPagesForTocEntry";




  //BookPageSize in webRawLogs is 4800, here we don't have that Info
  public static final String BOOK_OFFSET_IN_CHAPTER = "BookPageScreen";//TODO confirm
  public static final String BOOK_CHAPTER_SIZE = "BookPageScreenSize";//TODO confirm
  public static final String BOOK_CHAPTER_PROGRESS = "BookPageScreenProgress";//TODO confirm
  public static final String BOOK_PROGRESS = "BookProgress";//TODO confirm





  public static class Reader {
    public static final String OPEN_READER = "Open";
  }



}
