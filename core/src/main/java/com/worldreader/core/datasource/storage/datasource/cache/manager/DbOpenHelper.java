package com.worldreader.core.datasource.storage.datasource.cache.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.CacheTableMeta;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class DbOpenHelper extends SQLiteOpenHelper {

  private static final String DB_NAME = "worldreader_cache";
  private static final int DB_VERSION = 1;

  @Inject public DbOpenHelper(@NonNull Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  @NonNull private static String getCreateCacheTableQuery() {
    return "CREATE TABLE "
        + CacheTableMeta.TABLE
        + "("
        + CacheTableMeta.COLUMN_KEY
        + " TEXT NOT NULL PRIMARY KEY, "
        + CacheTableMeta.COLUMN_VALUE
        + " TEXT NOT NULL, "
        + CacheTableMeta.COLUMN_TIMESTAMP
        + " INTEGER NOT NULL"
        + ");";
  }

  @Override public void onCreate(@NonNull SQLiteDatabase db) {
    db.execSQL(getCreateCacheTableQuery());
  }

  @Override public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
