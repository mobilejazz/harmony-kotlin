package com.worldreader.core.datasource.storage.security;

import android.content.Context;
import com.mobilejazz.logger.library.Logger;
import com.securepreferences.SecurePreferences;

public class KeyManagerFactory {

  public static KeyManager create(Context context, Logger logger) {
    KeyManagerPreferences deprecatedKeyManagerPreferences = KeyManagerPreferences.create(context);

    SecurePreferences newSecurePreferences;
    try {
      newSecurePreferences = new SecurePreferences(context);
    } catch (Exception e) {
      // Something bad happened while trying to build this new version, so we need to fallback
      // to the least common denominator
      return new LessSecurePreferencesKeyManagerImpl(context,
          new LessSecurePreferences(context, logger));
    }

    MigrationWrapperPreferences migrationWrapperPreferences =
        MigrationWrapperPreferences.create(MigrationWrapperPreferences.Implementation.OLD,
            deprecatedKeyManagerPreferences, newSecurePreferences);

    return new SecurePreferencesKeyManagerImpl(context, migrationWrapperPreferences);

    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    //  try {
    //    return new AndroidKeyManagerImpl(context, migrationWrapperPreferences);
    //  } catch (KeyStoreException e) {
    //    return new SecurePreferencesKeyManagerImpl(context, migrationWrapperPreferences);
    //  }
    //} else {
    //  return new SecurePreferencesKeyManagerImpl(context, migrationWrapperPreferences);
    //}
  }
}
