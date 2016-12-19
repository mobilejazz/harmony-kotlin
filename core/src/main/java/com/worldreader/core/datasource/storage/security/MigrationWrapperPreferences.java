package com.worldreader.core.datasource.storage.security;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.*;

public class MigrationWrapperPreferences implements SharedPreferences {

  public enum Implementation {
    NEW,
    OLD
  }

  private volatile Implementation implementation;

  private final SharedPreferences oldPreferences;
  private final SharedPreferences newPreferences;

  public static MigrationWrapperPreferences create(@NonNull Implementation defaultImplementation,
      @NonNull SharedPreferences oldPreferences, @NonNull SharedPreferences newPreferences) {
    return new MigrationWrapperPreferences(defaultImplementation, oldPreferences, newPreferences);
  }

  private MigrationWrapperPreferences(Implementation implementation,
      SharedPreferences oldPreferences, SharedPreferences newPreferences) {
    this.implementation = implementation;
    this.oldPreferences = oldPreferences;
    this.newPreferences = newPreferences;
  }

  public synchronized void use(Implementation implementation) {
    this.implementation = implementation;
  }

  public Implementation getCurrentImplementation() {
    return implementation;
  }

  @Override public Map<String, ?> getAll() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Nullable @Override public String getString(String key, String defValue) {
    if (this.implementation == Implementation.OLD) {
      return oldPreferences.getString(key, defValue);
    } else {
      return newPreferences.getString(key, defValue);
    }
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
    return false;
  }

  @Override public Editor edit() {
    if (this.implementation == Implementation.OLD) {
      return oldPreferences.edit();
    } else {
      return newPreferences.edit();
    }
  }

  @Override
  public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override public void unregisterOnSharedPreferenceChangeListener(
      OnSharedPreferenceChangeListener listener) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  public class MigrationEditor implements Editor {

    @Override public Editor putString(String key, String value) {
      if (implementation == Implementation.OLD) {
        return oldPreferences.edit().putString(key, value);
      } else {
        return newPreferences.edit().putString(key, value);
      }
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
      if (implementation == Implementation.OLD) {
        return oldPreferences.edit().remove(key);
      } else {
        return newPreferences.edit().remove(key);
      }
    }

    @Override public Editor clear() {
      if (implementation == Implementation.OLD) {
        return oldPreferences.edit().clear();
      } else {
        return newPreferences.edit().clear();
      }
    }

    @Override public boolean commit() {
      if (implementation == Implementation.OLD) {
        return oldPreferences.edit().commit();
      } else {
        return newPreferences.edit().commit();
      }
    }

    @Override public void apply() {
      throw new UnsupportedOperationException("Not implemented!");
    }
  }

}
