package com.worldreader.core.datasource.storage.security;

import android.support.annotation.NonNull;
import com.worldreader.core.datasource.storage.security.exception.KeyNotFoundException;

import java.security.KeyStore;

/**
 * A simplistic key manager that uses {@link ObfuscatedPreferences} for generating encrypted data
 * in
 * Android pre 4.4 and for 4.4 onwards it uses {@link KeyStore} APIs.
 */
// @NotThreadSafe
public interface KeyManager {

  /** Creates a non null {@link String} with a random content inside in hexadecimal form. */
  @NonNull String generateRandomKey();

  /**
   * Stores the key inside the preferences obfuscated.
   *
   * @param key The value you want to encrypt.
   */
  boolean storeKey(@NonNull String key);

  /**
   * Retrieves the encrypted key if exists.
   *
   * @throws KeyNotFoundException if the value is not found!
   */
  String retrieveKey() throws KeyNotFoundException;

  /** Checks if there's one key created or not. */
  boolean existsKey();
}