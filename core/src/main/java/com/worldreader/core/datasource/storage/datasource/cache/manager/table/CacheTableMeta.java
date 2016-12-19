package com.worldreader.core.datasource.storage.datasource.cache.manager.table;

import android.support.annotation.NonNull;
import com.pushtorefresh.storio.sqlite.queries.Query;

public class CacheTableMeta {

  @NonNull public static final String TABLE = "cache";

  @NonNull public static final String COLUMN_KEY = "key";

  @NonNull public static final String COLUMN_VALUE = "value";

  @NonNull public static final String COLUMN_TIMESTAMP = "timestamp";

  @NonNull public static final Query QUERY_ALL = Query.builder().table(TABLE).build();

  private CacheTableMeta() {
    throw new IllegalStateException("No instances for this class");
  }
}
