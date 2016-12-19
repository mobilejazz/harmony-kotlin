package com.worldreader.core.datasource.storage.datasource.cache.manager.entity;

import android.support.annotation.NonNull;

public class CacheBytes {

  private @NonNull String key;
  private byte[] value;
  private long timestamp;

  public CacheBytes() {
  }

  private CacheBytes(@NonNull String key, byte[] value, long timestamp) {
    this.key = key;
    this.value = value;
    this.timestamp = timestamp;
  }

  public byte[] getValue() {
    return value;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getKey() {
    return key;
  }

  public static CacheBytes create(String key, byte[] value, long timestamp) {
    return new CacheBytes(key, value, timestamp);
  }
}
