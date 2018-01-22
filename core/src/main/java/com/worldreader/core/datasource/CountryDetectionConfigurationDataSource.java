package com.worldreader.core.datasource;

import com.worldreader.core.domain.model.CountryDetectionConfiguration;

/**
 * There is no need for a repository for the {@link com.worldreader.core.domain.model.CountryDetectionConfiguration}.
 * So this DataSource interface will only be implemented as SharedPreference storage
 */
public interface CountryDetectionConfigurationDataSource {

  CountryDetectionConfiguration get();

  void put(CountryDetectionConfiguration countryDetectionConfiguration);
}
