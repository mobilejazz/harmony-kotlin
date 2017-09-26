package com.worldreader.core.analytics.amazon;

public final class AmazonMobileAnalyticsConstants {

  public static final String BOOK_DETAILS_EVENT = "BookDetails";
  public static final String BOOK_LIKE_EVENT = "Like";
  public static final String BOOK_UNLIKE_EVENT = "Unlike";
  public static final String BOOK_ADD_TO_LIBRARY_EVENT = "AddUserBook";
  public static final String BOOK_REMOVE_FROM_LIBRARY_EVENT = "RemoveUserBook";
  public static final String BOOK_DOWNLOADED_EVENT = "BookDownloaded";
  public static final String STARTING_BOOK_DOWNLOAD_EVENT = "StartingBookDownload";
  public static final String BOOK_EXCEEDED_DOWNLOAD_LIMIT_EVENT = "ExceededDownloadLimit";
  public static final String BOOK_DELETE_DOWNLOADED_BOOK_EVENT = "DeleteDownload";
  public static final String ADD_COLLECTION_TO_LIBRARY_EVENT = "AddCollectionToLibrary";
  public static final String REMOVE_COLLECTION_FROM_LIBRARY_EVENT = "RemoveCollectionFromLibrary";
  public static final String MORE_BOOKS_EVENT = "More";
  public static final String LOAD_MORE_BOOKS_EVENT = "LoadMore";//Scrolling down the list of books
  public static final String COLLECTIONS_EVENT = "Collections";
  public static final String CATEGORY_EVENT = "CategoryDetails";
  public static final String CATEGORY_SELECTION_EVENT = "CategorySelection";
  public static final String CATEGORY_SELECTED_ATTRIBUTE = "categorySelected";
  public static final String PARENT_CATEGORY_ID_ATTRIBUTE = "parentCategoryId";
  public static final String PARENT_CATEGORY_TITLE_ATTRIBUTE = "parentCategoryTitle";

  //Kenya LEAP Program events
  public static final String LOCAL_LIBRARY_SELECTED_EVENT = "LocalLibrarySelected";
  public static final String LOCAL_LIBRARY_NAME_ATTRIBUTE = "localLibraryName";
  public static final String LOCAL_LIBRARY_UUID_ATTRIBUTE = "localLibraryUuid";

  public static final String SEARCH_EVENT = "Search";
  public static final String SIGNUP_EVENT = "Signup";
  public static final String LOGIN_EVENT = "Login";
  public static final String REGISTER_ATTRIBUTE = "register";
  public static final String ANONYMOUS_USAGE_EVENT = "AnonymousUsage";
  public static final String SAVE_GOALS_EVENT = "SaveGoals";
  public static final String PAGES_PER_DAY_ATTRIBUTE = "pagesPerDayGoal";
  public static final String BOOK_FINISHED_EVENT = "BookFinished";
  public static final String READ_IN_LANGUAGE_EVENT = "ReadInLanguage";
  public static final String LANGUAGE_ISO3_ATTRIBUTE = "language";
  public static final String LANGUAGE_NAME_ATTRIBUTE = "languageName";
  public static final String SEE_LEADER_BOAD_EVENT = "LeaderBoard";
  public static final String LEADER_BOAD_PERIOD_ATTRIBUTE = "leaderPeriod";
  public static final String SEE_MILESTONES_EVENT = "SeeMilestones";
  public static final String MILESTONES_AS_USERTYPE_ATTRIBUTE = "asUserType";
  public static final String MILESTONE_CLICKED_EVENT = "MilestoneClicked";
  public static final String MILESTONE_ATTRIBUTE = "milestone";
  public static final String MILESTONE_STATE_ATTRIBUTE = "milestoneState";
  public static final String MILESTONE_DESC_ATTRIBUTE = "milestoneDesc";
  public static final String SHARE_BADGE_EVENT = "ShareBadge";
  public static final String BOOK_CONTINUE_READING_EVENT = "ContinueReading";

  public static final String SEARCH_RESULT_SIZE_ATTRIBUTE = "searchResultsSize";

  public static final String BOOK_ID_ATTRIBUTE = "bookId";
  public static final String BOOK_VERSION_ATTRIBUTE = "bookVersion";
  public static final String BOOK_TITLE_ATTRIBUTE = "bookTitle";
  public static final String CATEGORY_ID_ATTRIBUTE = "categoryId";
  public static final String SHELVE_ATTRIBUTE = "sortingCriteria";
  public static final String SHELVE_TITLE_ATTRIBUTE = "sortingCriteriaTitle";
  public static final String COLLECTION_ID_ATTRIBUTE = "collectionId";
  public static final String COLLECTION_TITLE_ATTRIBUTE = "collectionTitle";

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

  ///REVIEW FROM HERE ON----------------
  public static final String RAW_CONTROLLER = "Controller";
  public static final String RC_ACCOUNT = "Account";
  public static final String RC_AUTHOR = "Author";
  public static final String RC_BOOK = "Book";
  public static final String RC_CATEGORY = "Category";
  public static final String RC_COLLECTION = "Collection";
  public static final String RC_HOME = "Home";
  public static final String RC_LIBRARY = "Library";
  public static final String RC_MANAGE = "Manage";
  public static final String RC_PROGRESS = "Progress";
  public static final String RC_SEARCH = "SEARCH";
  public static final String RC_SHARE = "Share";
  //Controller: Site, Actions: BookMigrated, BookNotFound, Oops
  //Controller: Survey, Action: SubmintAjax
  public static final String RC_TOGHETER = "Together";
  //Welcome
  //ShareReturn

  public static final String RAW_ACTION = "Action";
  public static final String RA_INDEX = "Index";//Controller:Together, Action:Index.
  // Controller:Account Action:Index, Controller:Author Action:Index,
  // Controller:Category Action:Index, Controller: Home Action:Index, Controller:Library, Action: Index ->Event: More_Books_Library,
  // Controller:Search, Action:Index
  public static final String RA_LOGIN = "Login";//Controller:Account Action:Login
  public static final String RA_LOGOFF = "LogOff";//Controller:Account Action:LogOff
  public static final String RA_CHANGE_FONT_SIZE = "ChangeFontSize";//Controller:Account Action:ChangeFontSize
  public static final String RA_REGISTER = "Register";//Controller:Account Action:Register
  public static final String RA_FORGOT_PASSWORD = "ForgotPassword";//Controller:Account Action:ForgotPassword
  public static final String RA_SET_CULTURE = "SetCulture";//Controller:Home Action:SetCulture

  public static final String RA_DETAILS = "Details"; //Controller: Book, Action: Details, Controller: Category, Action: Details
  public static final String RA_PAGE = "Page"; //Controller: Book, Action: Page
  public static final String RA_BOOKS = "Books"; //Controller: Category, Action: Books -> Event: More
  public static final String RA_EMAIL_SHARE = "EmailShare"; //Controller:Book Action:EmailShare
  public static final String RA_LIKE = "Like"; //Controller:Book Action:Like
  public static final String RA_READ = "Read"; //Controller:Book Action:Read
  public static final String RA_SAVE = "Save"; //Controller:Book Action:Save -> Event:BookDownload
  public static final String RA_DELETE_SAVED = "DeleteSavedBook"; //Controller:Book Action:Save ->Event:DeleteDowload
  public static final String RA_TOC = "TableOfContents"; //Controller:Book Action:TableOfContents
  public static final String RA_UNLIKE = "UnLike"; //Controller:Book Action:Unlike
  public static final String RA_ADD_USER_BOOK = "AddUserBook";//Controller: Library, Action:AddUserBook ->Event:AddUserBook
  public static final String RA_REMOVE_USER_BOOK = "RemoveUserBook";//Controller: Library, Action:RemoveUserBook ->Event:RemoveUserBook
  public static final String RA_TOGGLE_BOOK_FAVORITE = "ToggleBookFavorite"; //Controller: Library, Action:ToggleBookFavorite
  public static final String RA_MANAGE_CHANGE_PASSWORD = "ChangePassword"; //Controller: Manage, Action: ChangePassword

  public static final String RA_SHARE_WA = "WhatsApp";//Controller:Share Action:WhatsApp
  public static final String RA_TIPS = "Tips";//Controller: Together, Action:Tips
  public static final String RA_REQUEST = "Request";//Controller: Serach,  Action: Request
  public static final String RA_RESULTS = "Req_Results"; //Controller: Search, Action: Results
  public static final String RA_RESULTS_CLICK = "ResultClick";
  public static final String RESULT_SIZE = "ResultsSize";

  public static final String TIPS_EVENT = "Tips";

  public static final String GO_TO_SHARE_EVENT = "GoToShare";

  public static final String GO_TO_HOME_EVENT = "GoToHome";
  public static final String GO_TO_MY_LIBARY = "GoToMyLibrary";
  public static final String GO_TO_OFFLINE_BOOKS = "GoToOfflineBooks";
  public static final String GO_TO_SETTINGS = "GoToSettings";
  public static final String GO_TO_SEARCH = "GoToSearch";
  public static final String GO_TO_BOOK_DETAILS_FROM_SERACH_RESULT = "GoToBookDetailsFromSerachResults";
  public static final String GO_TO_PROFILE = "GoToProfile";
  public static final String GO_TO_SIGNUP_EVENT = "GoToRegister";
  public static final String GO_TO_LOGIN = "GoToLogin";
  public static final String MY_BOOKS_EVENT = "MyBooks";

  public static String getBookVersionIntValue(String bookVersion) {
    return bookVersion == null ? "" : bookVersion.equalsIgnoreCase("Latest") ? "-1" : bookVersion;
  }

}
