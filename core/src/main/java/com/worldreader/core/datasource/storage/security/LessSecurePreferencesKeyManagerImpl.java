package com.worldreader.core.datasource.storage.security;

import android.content.Context;
import android.support.annotation.NonNull;
import com.worldreader.core.datasource.storage.security.exception.KeyNotFoundException;

public class LessSecurePreferencesKeyManagerImpl extends AbstractKeyManager<LessSecurePreferences> {

  public LessSecurePreferencesKeyManagerImpl(Context context, LessSecurePreferences preferences) {
    super(context, preferences);
  }

  @Override public boolean storeKey(@NonNull String key) {
    return preferences.edit().putString(KEY, key).commit();
  }

  @Override public String retrieveKey() throws KeyNotFoundException {
    String key = preferences.getString(KEY, null);

    if (key == null) {
      throw new KeyNotFoundException();
    }

    return key;
  }

  @Override public boolean existsKey() {
    return preferences.getString(KEY, null) != null;
  }
}