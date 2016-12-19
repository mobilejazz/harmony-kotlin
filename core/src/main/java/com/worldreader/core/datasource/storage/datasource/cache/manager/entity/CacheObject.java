package com.worldreader.core.datasource.storage.datasource.cache.manager.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.CacheTableMeta;
import com.worldreader.core.datasource.storage.datasource.cache.strategy.timestamp.TimestampCachingObject;

@StorIOSQLiteType(table = CacheTableMeta.TABLE) public class CacheObject
    implements TimestampCachingObject {

  @Nullable @StorIOSQLiteColumn(name = CacheTableMeta.COLUMN_KEY, key = true) String key;

  @NonNull @StorIOSQLiteColumn(name = CacheTableMeta.COLUMN_VALUE) String value;

  @StorIOSQLiteColumn(name = CacheTableMeta.COLUMN_TIMESTAMP) long timestamp;

  public CacheObject() {
  }

  private CacheObject(String key, @NonNull String value, long timestamp) {
    this.key = key;
    this.value = value;
    this.timestamp = timestamp;
  }

  public static CacheObject newCacheObject(@NonNull String key, @NonNull String value,
      long timestamp) {
    return new CacheObject(key, value, timestamp);
  }

  @Nullable public String getKey() {
    return key;
  }

  @NonNull public String getValue() {
    return value;
  }

  @NonNull @Override public long getTimestamp() {
    return timestamp;
  }

}
