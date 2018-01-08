package com.worldreader.core.datasource;

import com.worldreader.core.domain.model.Referrer;

/**
 * There is no need for a repository for the {@link Referrer}.
 * So this DataSource interface will only be implemented as SharedPreference storage
 */
public interface ReferrerDataSource {

  void put(Referrer referrer);

  Referrer get();
}
