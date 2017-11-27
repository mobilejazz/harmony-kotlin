package com.worldreader.core.datasource.helper.locale;

import com.google.common.base.Optional;

public interface CountryCodeProvider {

  String getIPAddress(boolean useIPv4);

  String getCountryCode();

  Optional<String> getGeolocationCountryIsoCode();

  String getNetworkCountryIsoCode();

  String getSimCountryIsoCode();

  String getCountryIso3Code();

  String getCountry();

  String getLanguageIso3Code();

  String getDisplayLanguage();

  String getDisplayLanguage(String languageIsoCode3);
}
