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
  public static final String ADD_COLLECTION_TO_LIBRARY_EVENT = "AddCollectionToLibrary";
  public static final String REMOVE_COLLECTION_FROM_LIBRARY_EVENT = "RemoveCollectionFromLibrary";
  public static final String MORE_BOOKS_EVENT = "More";
  public static final String LOAD_MORE_BOOKS_EVENT = "LoadMore";//Scrolling down the list of books
  public static final String COLLECTIONS_EVENT = "Collections";
  public static final String CATEGORIES_EVENT = "Categories";
  public static final String CATEGORY_EVENT = "CategoryDetails";
  public static final String SUBCATEGORY_EVENT = "SubCategorySelection";
  public static final String CATEGORY_SELECTION_EVENT = "CategorySelection";
  public static final String CATEGORY_SELECTED_ATTRIBUTE = "CategorySelected";
  public static final String PARENT_CATEGORY_ID_ATTRIBUTE = "ParentCategoryId";
  public static final String PARENT_CATEGORY_TITLE_ATTRIBUTE = "ParentCategoryTitle";

  //Kenya LEAP Program events
  public static final String LOCAL_LIBRARY_SELECTED_EVENT = "LocalLibrarySelected";
  public static final String LOCAL_LIBRARY_NAME_ATTRIBUTE = "LocalLibraryName";
  public static final String LOCAL_LIBRARY_UUID_ATTRIBUTE = "LocalLibraryUuid";



  public static final String SEARCH_EVENT = "Search";
  public static final String SIGNUP_EVENT = "Signup";
  public static final String LOGIN_EVENT = "Login";
  public static final String REGISTER_ATTRIBUTE = "Register";
  public static final String ANONYMOUS_USAGE_EVENT = "AnonymousUsage";
  public static final String SAVE_GOALS_EVENT = "SaveGoals";
  public static final String PAGES_PER_DAY_ATTRIBUTE = "PagesPerDayGoal";
  public static final String BOOK_FINISHED_EVENT = "BookFinished";
  public static final String READ_IN_LANGUAGE_EVENT = "ReadInLanguage";
  public static final String LANGUAGE_ISO3_ATTRIBUTE = "Language";
  public static final String LANGUAGE_NAME_ATTRIBUTE = "LanguageName";
  public static final String SEE_LEADER_BOAD_EVENT = "LeaderBoard";
  public static final String LEADER_BOAD_PERIOD_ATTRIBUTE = "LeaderPeriod";
  public static final String SEE_MILESTONES_EVENT ="SeeMilestones";
  public static final String MILESTONES_AS_USERTYPE_ATTRIBUTE ="AsUserType";
  public static final String MILESTONE_CLICKED_EVENT = "MilestoneClicked";
  public static final String MILESTONE_ATTRIBUTE ="Milestone";
  public static final String MILESTONE_STATE_ATTRIBUTE = "MilestoneState";
  public static final String MILESTONE_DESC_ATTRIBUTE ="MilestoneDesc";
  public static final String SHARE_BADGE_EVENT = "ShareBadge";


  public static final String SEARCH_RESULT_SIZE_ATTRIBUTE ="SearchResultsSize";

  public static final String BOOK_ID_ATTRIBUTE = "BookId";
  public static final String BOOK_VERSION_ATTRIBUTE = "BookVersion";
  public static final String BOOK_TITLE_ATTRIBUTE = "BookTitle";
  public static final String CATEGORY_ID_ATTRIBUTE = "CategoryId";
  public static final String SHELVE_ATTRIBUTE = "Shelve";
  public static final String SHELVE_TITLE_ATTRIBUTE = "ShelveTitle";
  public static final String COLLECTION_ID_ATTRIBUTE = "CollectionId";
  public static final String COLLECTION_TITLE_ATTRIBUTE = "CollectionTitle";

  public static final String CATEGORY_TITLE_ATTRIBUTE = "CategoryTitle";
  public static final String SEARCH_BY_ATTRIBUTE = "SearchBy";
  public static final String SEARCH_BY_TITLE = "Title";
  public static final String SEARCH_BY_AUTHOR = "Author";
  public static final String SEARCH_QUERY_ATTRIBUTE = "Query";
  public static final String OFFSET_ATTRIBUTE = "Offset";
  public static final String LIMIT_ATTRIBUTE = "Limit";
  public static final String CLICKED_IN_ATTRIBUTE = "ClickedIn";//Examples of use of this attribute. BookDetails can be accessed from many different
  // places. When clickin on a book on ReadingTips page, I'll add this attribute with value ReadingTips. We can messure the amount of people
  // accessing book details from there.
  public static final String CLICKED_IN_EXTRA = "ClickedInExtraInfo";

  public static final String IN_SCREEN = "InScreen";
  public static final String SCREEN_NAME_ATTRIBUTE = "ScreenName";

  public static String getBookVersionIntValue(String bookVersion){
    return bookVersion == null ? "" : bookVersion.equalsIgnoreCase("Latest") ? "-1" : bookVersion;
  }



}
