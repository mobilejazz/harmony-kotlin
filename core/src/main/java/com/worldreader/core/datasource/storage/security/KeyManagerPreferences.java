package com.worldreader.core.datasource.storage.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;

import java.util.*;

/**
 * A wrapper class that uses {@link ObfuscatedPreferences} if android version is below 4.4 or
 * normal preferences if version is after 4.4.
 */
@Deprecated public class KeyManagerPreferences implements SharedPreferences {

  private final SharedPreferences preferences;
  private final Editor editor;

  public static KeyManagerPreferences create(Context context) {
    SharedPreferences sharedPreferences =
        context.getSharedPreferences(KeyManagerPreferences.class.getCanonicalName(),
            Context.MODE_PRIVATE);

    Editor editor = KeyManagerEditor.wrap(sharedPreferences.edit());

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      sharedPreferences = ObfuscatedPreferences.wrap(context, sharedPreferences, editor,
          ObfuscatedPreferences.DEFAULT_SALT);
    }

    return new KeyManagerPreferences(sharedPreferences, editor);
  }

  private KeyManagerPreferences(SharedPreferences preferences, Editor editor) {
    this.preferences = preferences;
    this.editor = editor;
  }

  @Override public Map<String, ?> getAll() {
    return preferences.getAll();
  }

  @Nullable @Override public String getString(String key, String defValue) {
    return preferences.getString(key, defValue);
  }

  @Nullable @Override public Set<String> getStringSet(String key, Set<String> defValues) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override public int getInt(String key, int defValue) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override public long getLong(String key, long defValue) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override public float getFloat(String key, float defValue) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override public boolean getBoolean(String key, boolean defValue) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override public boolean contains(String key) {
    return preferences.contains(key);
  }

  @Override public Editor edit() {
    return this.editor;
  }

  @Override
  public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override public void unregisterOnSharedPreferenceChangeListener(
      OnSharedPreferenceChangeListener listener) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public static class KeyManagerEditor implements Editor {

    private final Editor editor;

    public static KeyManagerEditor wrap(Editor editor) {
      return new KeyManagerEditor(editor);
    }

    private KeyManagerEditor(Editor editor) {
      this.editor = editor;
    }

    @Override public Editor putString(String key, String value) {
      return editor.putString(key, value);
    }

    @Override public Editor putStringSet(String key, Set<String> values) {
      throw new UnsupportedOperationException("Not implemented!");
    }

    @Override public Editor putInt(String key, int value) {
      throw new UnsupportedOperationException("Not implemented!");
    }

    @Override public Editor putLong(String key, long value) {
      throw new UnsupportedOperationException("Not implemented!");
    }

    @Override public Editor putFloat(String key, float value) {
      throw new UnsupportedOperationException("Not implemented!");
    }

    @Override public Editor putBoolean(String key, boolean value) {
      throw new UnsupportedOperationException("Not implemented!");
    }

    @Override public Editor remove(String key) {
      throw new UnsupportedOperationException("Not implemented!");
    }

    @Override public Editor clear() {
      return editor.clear();
    }

    @Override public boolean commit() {
      return this.editor.commit();
    }

    @Override public void apply() {
      throw new UnsupportedOperationException("Not implemented!");
    }
  }
}
