package com.worldreader.core.common.helper;

import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class CountryChecker {

  private CountryCodeProvider countryCodeProvider;

  @Inject public CountryChecker(CountryCodeProvider countryCodeProvider) {
    this.countryCodeProvider = countryCodeProvider;
  }

  public boolean isLocatedInAnyCountry(String... countryIso2Codes) {
    boolean isLocatedInAnyCountry = false;
    for (String countryIso2Code : countryIso2Codes) {
      isLocatedInAnyCountry = isLocatedInCountry(countryIso2Code);
      if (isLocatedInAnyCountry) {
        break;
      }
    }
    return isLocatedInAnyCountry;
  }

  public boolean isLocatedInCountry(String countryIso2Code) {
    return countryIso2Code.equalsIgnoreCase(countryCodeProvider.getCountryCode());
  }
}