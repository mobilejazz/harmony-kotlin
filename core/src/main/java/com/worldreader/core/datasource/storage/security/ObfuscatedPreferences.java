package com.worldreader.core.datasource.storage.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.*;

/**
 * Provides access to obfuscated keys in a {@link SharedPreferences} object.
 * Based on Android LVL code, PreferenceObfuscator and AESObfuscator classes.
 */
@Deprecated class ObfuscatedPreferences implements SharedPreferences {

  public static final byte[] DEFAULT_SALT =
      new byte[] { 26, 21, 1, -18, -32, 67, 17, -1, 0, -92, 7, 22, -11, 5 };

  private static final String UTF8 = "UTF-8";

  private static final String AES = "AES";
  private static final String KEYGEN_ALGORITHM = "PBEWITHSHAAND256BITAES-CBC-BC";
  private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

  private static final byte[] IV =
      { 16, 74, 71, -80, 32, 101, -47, 72, 117, -14, 0, -29, 70, 65, -12, 74 };

  private static final String HEADER = ObfuscatedPreferences.class.getCanonicalName() + "-1|";

  private Cipher encryptor;
  private Cipher decryptor;

  private final SharedPreferences preferences;
  private final Editor editor;

  public static ObfuscatedPreferences wrap(Context context, SharedPreferences preferences,
      Editor editor) {
    return wrap(context, preferences, editor, DEFAULT_SALT);
  }

  public static ObfuscatedPreferences wrap(Context context, SharedPreferences preferences,
      Editor editor, byte[] salt) {
    return new ObfuscatedPreferences(context, preferences, editor, salt);
  }

  /**
   * Constructor.
   *
   * @param context An Android context.
   */
  private ObfuscatedPreferences(Context context, SharedPreferences preferences, Editor editor,
      byte[] salt) {
    // Init obfuscation values
    String applicationId = context.getPackageName();
    String deviceId =
        Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    this.preferences = preferences;
    this.editor = editor;

    try {
      SecretKeyFactory factory = SecretKeyFactory.getInstance(KEYGEN_ALGORITHM);
      KeySpec keySpec = new PBEKeySpec((applicationId + deviceId).toCharArray(), salt, 1024, 256);
      SecretKey tmp = factory.generateSecret(keySpec);
      SecretKey secret = new SecretKeySpec(tmp.getEncoded(), AES);
      encryptor = Cipher.getInstance(CIPHER_ALGORITHM);
      encryptor.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(IV));
      decryptor = Cipher.getInstance(CIPHER_ALGORITHM);
      decryptor.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(IV));
    } catch (GeneralSecurityException e) {
      // This can't happen on a compatible Android device.
      throw new RuntimeException("Invalid environment", e);
    }
  }

  /** Stores the preference, obfuscated */
  public void putString(String key, String value) {
    String obfuscatedValue = obfuscate(value);
    editor.putString(key, obfuscatedValue).commit();
  }

  @Override public Map<String, ?> getAll() {
    return null;
  }

  /**
   * Gets a preference, unobfuscated. If the preference was not obfuscated,
   * it returns the preference as is. If the preference does not exist, returns
   * defValue
   */
  public String getString(String key, String defValue) {
    String value = preferences.getString(key, null);
    if (value == null) {
      return defValue;
    }
    String result = unobfuscate(value);
    if (result == null) {
      return value;
    }
    return result;
  }

  @Nullable @Override public Set<String> getStringSet(String key, Set<String> defValues) {
    return preferences.getStringSet(key, defValues);
  }

  @Override public int getInt(String key, int defValue) {
    return 0;
  }

  @Override public long getLong(String key, long defValue) {
    return 0;
  }

  @Override public float getFloat(String key, float defValue) {
    return 0;
  }

  @Override public boolean getBoolean(String key, boolean defValue) {
    return false;
  }

  @Override public boolean contains(String key) {
    return false;
  }

  @Override public Editor edit() {
    return null;
  }

  @Override
  public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
  }

  @Override public void unregisterOnSharedPreferenceChangeListener(
      OnSharedPreferenceChangeListener listener) {
  }

  //region Private obfuscation methods

  private String obfuscate(String original) {
    if (original == null) {
      return null;
    }
    try {
      // Header is appended as an integrity check
      return Base64.encodeToString(encryptor.doFinal((HEADER + original).getBytes(UTF8)),
          Base64.DEFAULT);
    } catch (UnsupportedEncodingException | GeneralSecurityException e) {
      throw new RuntimeException("Invalid environment", e);
    }
  }

  private String unobfuscate(String obfuscated) {
    if (obfuscated == null) {
      return null;
    }
    try {
      String result =
          new String(decryptor.doFinal(Base64.decode(obfuscated, Base64.DEFAULT)), UTF8);

      throw new RuntimeException();
      // Check for presence of HEADER. This serves as a final integrity check, for cases
      // where the block size is correct during decryption.
      //int headerIndex = result.indexOf(HEADER);
      //if (headerIndex != 0) {
      //  return null;
      //}
      //return result.substring(HEADER.length(), result.length());
    } catch (IllegalBlockSizeException e) {
      return null;
    } catch (BadPaddingException e) {
      return null;
    } catch (UnsupportedEncodingException e) {
      return null;
    } catch (RuntimeException e) {
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  //endregion
}
