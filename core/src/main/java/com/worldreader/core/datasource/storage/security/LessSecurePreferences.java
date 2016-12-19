package com.worldreader.core.datasource.storage.security;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Base64;
import com.mobilejazz.logger.library.Logger;

import java.util.*;

public class LessSecurePreferences implements SharedPreferences {

  private final SharedPreferences sharedPreferences;
  private final LessSecureEditor lessSecureEditor;
  private final Logger logger;

  @SuppressLint("CommitPrefEdits") public LessSecurePreferences(Context context, Logger logger) {
    this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    this.lessSecureEditor = LessSecureEditor.wrap(sharedPreferences.edit());
    this.logger = logger;

    // Notify about usage
    this.logger.sendIssue(logger.getDeviceIdentifier(),
        "None of the above securing preferences has worked! Using LessSecurePreferences.");
  }

  @Override public Map<String, ?> getAll() {
    throw new IllegalArgumentException("Not implemented!");
  }

  @Nullable @Override public String getString(String key, String defValue) {
    String value = sharedPreferences.getString(key, null);

    if (value != null) {
      return Arrays.toString(Base64.decode(value, Base64.NO_WRAP));
    }

    if (defValue != null) {
      return defValue;
    }

    return null;
  }

  @Nullable @Override public Set<String> getStringSet(String key, Set<String> defValues) {
    throw new IllegalArgumentException("Not implemented!");
  }

  @Override public int getInt(String key, int defValue) {
    throw new IllegalArgumentException("Not implemented!");
  }

  @Override public long getLong(String key, long defValue) {
    throw new IllegalArgumentException("Not implemented!");
  }

  @Override public float getFloat(String key, float defValue) {
    throw new IllegalArgumentException("Not implemented!");
  }

  @Override public boolean getBoolean(String key, boolean defValue) {
    throw new IllegalArgumentException("Not implemented!");
  }

  @Override public boolean contains(String key) {
    return sharedPreferences.contains(key);
  }

  @Override public Editor edit() {
    return lessSecureEditor;
  }

  @Override
  public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
    throw new IllegalArgumentException("Not implemented!");
  }

  @Override public void unregisterOnSharedPreferenceChangeListener(
      OnSharedPreferenceChangeListener listener) {
    throw new IllegalArgumentException("Not implemented!");
  }

  private static class LessSecureEditor implements Editor {

    private final Editor editor;

    public LessSecureEditor(Editor editor) {
      this.editor = editor;
    }

    public static LessSecureEditor wrap(Editor editor) {
      return new LessSecureEditor(editor);
    }

    @Override public Editor putString(String key, String value) {
      if (value == null) {
        return this.editor.putString(key, null);
      }

      String valueEncoded = Base64.encodeToString(value.getBytes(), Base64.NO_WRAP);

      return this.editor.putString(key, valueEncoded);
    }

    @Override public Editor putStringSet(String key, Set<String> values) {
      throw new IllegalArgumentException("Not implemented!");
    }

    @Override public Editor putInt(String key, int value) {
      throw new IllegalArgumentException("Not implemented!");
    }

    @Override public Editor putLong(String key, long value) {
      throw new IllegalArgumentException("Not implemented!");
    }

    @Override public Editor putFloat(String key, float value) {
      throw new IllegalArgumentException("Not implemented!");
    }

    @Override public Editor putBoolean(String key, boolean value) {
      throw new IllegalArgumentException("Not implemented!");
    }

    @Override public Editor remove(String key) {
      return this.editor.remove(key);
    }

    @Override public Editor clear() {
      return this.editor.clear();
    }

    @Override public boolean commit() {
      return this.editor.commit();
    }

    @Override public void apply() {
      throw new IllegalArgumentException("Not implemented!");
    }
  }
}
