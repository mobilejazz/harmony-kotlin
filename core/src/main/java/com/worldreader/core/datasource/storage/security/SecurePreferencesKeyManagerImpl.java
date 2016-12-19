package com.worldreader.core.datasource.storage.security;

import android.content.Context;
import android.support.annotation.NonNull;
import com.worldreader.core.datasource.storage.security.exception.KeyNotFoundException;

public class SecurePreferencesKeyManagerImpl
    extends AbstractKeyManager<MigrationWrapperPreferences> {

  /**
   * A less secure version than AndroidSecureKeyManager. This is a fallback version when the
   * other one can't be used directly.
   * */
  public SecurePreferencesKeyManagerImpl(Context context, MigrationWrapperPreferences preferences) {
    super(context, preferences);
  }

  @Override public boolean storeKey(@NonNull String key) {
    // Always we are going to use the new implementation for new keys generated
    preferences.use(MigrationWrapperPreferences.Implementation.NEW);
    return preferences.edit().putString(KEY, key).commit();
  }

  @Override public String retrieveKey() throws KeyNotFoundException {
    // Since to retrieve a key it by design has to be used existsKey method, it is safe to assume
    // the old implementation doesn't have nothing

    preferences.use(MigrationWrapperPreferences.Implementation.NEW);
    String key = preferences.getString(KEY, null);

    if (key == null) {
      throw new KeyNotFoundException();
    }

    return key;
  }

  @Override public boolean existsKey() {
    preferences.use(MigrationWrapperPreferences.Implementation.NEW);

    boolean existsInNewImplementation = preferences.getString(KEY, null) != null;

    if (existsInNewImplementation) {
      return true;
    }

    preferences.use(MigrationWrapperPreferences.Implementation.OLD);

    String key = preferences.getString(KEY, null);

    if (key == null) {
      return false;
    }

    preferences.use(MigrationWrapperPreferences.Implementation.NEW);
    preferences.edit().putString(KEY, key).commit();
    performCleanupOldPreferences();

    return true;
  }

  private void performCleanupOldPreferences() {
    preferences.use(MigrationWrapperPreferences.Implementation.OLD);

    if (preferences.getString(KEY, null) != null) {
      preferences.edit().clear().commit();
    }
  }

}
