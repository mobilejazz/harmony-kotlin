package com.worldreader.core.datasource.storage.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.security.SecureRandom;

/** Base implementation class that only implements methods for creating a random key. */
public abstract class AbstractKeyManager<T extends SharedPreferences> implements KeyManager {

  protected static final String KEY = "key.manager";

  protected final Context context;
  protected final T preferences;

  public AbstractKeyManager(Context context, T preferences) {
    this.context = context.getApplicationContext();
    this.preferences = preferences;
  }

  @NonNull public String generateRandomKey() {
    byte[] bytes = new byte[16];
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(bytes);
    return bytesToHexString(bytes);
  }

  //region Private methods

  private static String bytesToHexString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte aByte : bytes) {
      sb.append(String.format("%02x", aByte));
    }
    return sb.toString();
  }

  //endregion
}
