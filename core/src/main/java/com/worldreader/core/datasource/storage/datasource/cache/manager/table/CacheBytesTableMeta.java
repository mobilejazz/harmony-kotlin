package com.worldreader.core.datasource.storage.datasource.cache.manager.table;

public class CacheBytesTableMeta {

  public static final String TABLE = "cache_byte";

  public static final String COLUMN_KEY = "key";

  public static final String COLUMN_VALUE = "value";

  public static final String COLUMN_TIMESTAMP = "timestamp";

  private CacheBytesTableMeta() {
    throw new IllegalStateException("No instances for this class");
  }

}
