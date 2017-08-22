package com.worldreader.core.datasource.helper.locale;

public interface CountryCodeProvider {

  String getIPAddress(boolean useIPv4);

  String getCountryCode();

  String getNetworkCountryIsoCode();

  String getSimCountryIsoCode();

  String getCountryIso3Code();

  String getLanguageIso3Code();

  String getDisplayLanguage();

  String getDisplayLanguage(String languageIsoCode3);
}
