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
  public static final String TOC_ENTRY_HREF = "tocEntryHref";
  public static final String TOC_ENTRY_SELECTED_EVENT = "GoToTocEntry";
  public static final String DICTIONARY_WORD_LOOKUP_EVENT = "DictionaryWordLookup";
  public static final String DICTIONARY_WORD_DEFINITION_NOT_FOUND_EVENT = "DictionaryWordDefinitionNotFound";
  public static final String LOOKUP_WORD_ATTRIBUTE = "LookupWord";

  public static final String BOOK_READING_SPINE_ELEM_IN_SPINE_POSITION = "bookCurrentlyReadingSpineElement";
  public static final String BOOK_READING_SPINE_ELEM_SIZE_IN_CHARS = "spineElementSizeInChars";
  public static final String BOOK_READING_SCREEN_TEXT_SIZE_IN_CHARS = "screenTextSizeInChars";
  public static final String BOOK_READING_CURRENT_PAGE_IN_SPINE_ELEM = "currentPageNumberInSpineElement";
  public static final String BOOK_READING_AMOUNT_OF_PAGES_IN_SPINE_ELEM = "amountOfPagesForSpineElement";
  public static final String COUNTRY ="Country";

  public static final String USER_ID = "userId";
  public static final String DEVICE_ID = "deviceId";
  public static final String CLIENT_ID = "clientId";
  public static final String COUNTRTY_CODE = "countryCode";
  public static final String GEOLOCATION_COUNTRY_CODE = "geolocationCountryCode";
  public static final String SIM_COUNTRY_CODE = "simCountryCode";
  public static final String NETWORK_COUNTRY_CODE = "networkCountryCode";
  public static final String DEVICE_IPV4 = "deviceIPV4";
  public static final String DEVICE_IPV6 = "deviceIPV6";
  public static final String LOCALE_LANG_CODE = "localeLanguageCode";
  public static final String REFERRER_USER_ID = "referrerUserId";
  public static final String REFERRER_DEVICE_ID = "referrerDeviceId";

  public static final String SEARCH_QUERY_ATTRIBUTE = "query";
  public static final String SEARCH_AGE_ATTRIBUTE = "searchAge";
  public static final String SEARCH_CATEGORY_ATTRIBUTE = "searchCategory";
  public static final String SEARCH_LANG_ATTRIBUTE = "searchLang";
  public static final String SEARCH_EVENT = "Search";
  public static final String CHANGE_FONT_TYPE_EVENT = "ChangeFontType";
  public static final String CHANGE_FONT_SIZE_EVENT = "ChangeFontSize";
  public static final String SELECTED_FONT = "SelectedFont" ;
  public static final String SELECTED_FONT_SIZE = "SelectedFontSize";
  public static final String OPEN_READER_OPTIONS="OpenReaderOptions";
  public static final String SELECTED_IMAGE = "SelectedImage";
  public static final String IMAGE_ZOOM_EVENT = "ImageZoom" ;

  public static final String DEEP_LINK = "DEEP_LINK";
  public static final String SEARCH_EVENT_JSON = "SearchJSON";

  /**
   *

   attributes.put("userId", model.userId);
   attributes.put("deviceId", model.deviceId);
   attributes.put("clientId", model.clientId);
   attributes.put(AnalyticsEventConstants.APP_IN_OFFLINE, String.valueOf((reachability.isReachable()) ? 0 : 1));
   attributes.put("countryCode", countryCodeProvider.getCountryCode()); //This is the logic to get this value: tries to get geo, if not available  ->
   // SIM, if not available -> default:US
   attributes.put("geolocationCountryCode", countryCodeProvider.getGeolocationCountryIsoCode().isPresent()
   ? countryCodeProvider.getGeolocationCountryIsoCode().get()
   : ""); //This value is the actual country code obtained using Google API with obtained lat,long from GPS. If
   // we couldn't obtain that info, empty value

   //When generating logs for Opera, I use only this attribute and disable the following 5 ones.

   attributes.put("simCountryCode", countryCodeProvider.getSimCountryIsoCode());
   attributes.put("networkCountryCode", countryCodeProvider.getNetworkCountryIsoCode());
   attributes.put("deviceIPV4", countryCodeProvider.getIPAddress(true));
   attributes.put("deviceIPV6", countryCodeProvider.getIPAddress(false));
   attributes.put("localeLanguageCode", countryCodeProvider.getLanguageIso3Code());

   */
}
