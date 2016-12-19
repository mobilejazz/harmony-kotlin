package com.worldreader.core.datasource.helper.locale;

public interface CountryCodeProvider {

  String getCountryCode();

  String getCountryIso3Code();

  String getLanguageIso3Code();

  String getDisplayLanguage();

  String getDisplayLanguage(String languageIsoCode3);
}
