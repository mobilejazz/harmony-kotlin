package com.worldreader.core.analytics.amazon;

public final class AmazonMobileAnalyticsConstants {

  public static final String BOOK_DETAILS_EVENT = "BookDetails";
  public static final String BOOK_LIKE_EVENT = "Like";
  public static final String BOOK_UNLIKE_EVENT = "Unlike";
  public static final String BOOK_ADD_TO_LIBRARY_EVENT = "AddUserBook";
  public static final String BOOK_REMOVE_FROM_LIBRARY_EVENT = "RemoveUserBook";
  public static final String BOOK_DOWNLOADED_EVENT = "BookDownloaded";
  public static final String STARTING_BOOK_DOWNLOAD_EVENT= "StartingBookDownload";
  public static final String BOOK_EXCEEDED_DOWNLOAD_LIMIT_EVENT= "ExceededDownloadLimit";
  public static final String BOOK_DELETE_DOWNLOADED_BOOK_EVENT= "DeleteDownload";
  public static final String MORE_BOOKS_EVENT = "More";
  public static final String LOAD_MORE_BOOKS_EVENT = "LoadMore";//Scrolling down the list of books
  public static final String CATEGORY_EVENT = "CategoryDetails";

  public static final String SEARCH_EVENT = "Search";//Test against prod books API
  public static final String SIGNUP_EVENT = "Signup";
  public static final String LOGIN_EVENT = "Login";
  public static final String REGISTER_ATTRIBUTE = "register";
  public static final String ANONYMOUS_USAGE_EVENT = "AnonymousUsage";
  public static final String BOOK_FINISHED_EVENT = "BookFinished";
  public static final String READ_IN_LANGUAGE_EVENT = "ReadInLanguage";
  public static final String LANGUAGE_ISO3_ATTRIBUTE = "language";
  public static final String LANGUAGE_NAME_ATTRIBUTE = "languageName";
  public static final String BOOK_CONTINUE_READING_EVENT = "ContinueReading";

  public static final String SEARCH_RESULT_SIZE_ATTRIBUTE = "searchResultsSize";

  public static final String BOOK_ID_ATTRIBUTE = "bookId";
  public static final String BOOK_VERSION_ATTRIBUTE = "bookVersion";
  public static final String BOOK_TITLE_ATTRIBUTE = "bookTitle";
  public static final String CATEGORY_ID_ATTRIBUTE = "categoryId";
  public static final String SHELVE_ATTRIBUTE = "sortingCriteria";
  public static final String SHELVE_TITLE_ATTRIBUTE = "sortingCriteriaTitle";

  public static final String CATEGORY_TITLE_ATTRIBUTE = "categoryTitle";
  public static final String SEARCH_BY_ATTRIBUTE = "searchBy";
  public static final String SEARCH_BY_TITLE = "title";
  public static final String SEARCH_BY_AUTHOR = "author";
  public static final String SEARCH_QUERY_ATTRIBUTE = "query";
  public static final String OFFSET_ATTRIBUTE = "offset";
  public static final String LIMIT_ATTRIBUTE = "limit";
  public static final String REFERRING_SCREEN = "referringScreen";//Examples of use of this attribute. BookDetails can be accessed from many different
  // places. When clickin on a book on ReadingTips page, I'll add this attribute with value ReadingTips. We can messure the amount of people
  // accessing book details from there.
  public static final String REFERRING_META = "referringMeta";

  public static final String IN_SCREEN = "inScreen";
  public static final String SCREEN_NAME_ATTRIBUTE = "screenName";


  public static String getBookVersionIntValue(String bookVersion) {
    return bookVersion == null ? "" : bookVersion.equalsIgnoreCase("Latest") ? "-1" : bookVersion;
  }

}
