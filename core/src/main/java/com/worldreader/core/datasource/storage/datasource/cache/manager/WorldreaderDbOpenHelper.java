package com.worldreader.core.datasource.storage.datasource.cache.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBookLikesTable;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBooksTable;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserMilestonesTable;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserScoreTable;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UsersTable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class WorldreaderDbOpenHelper extends SQLiteOpenHelper {

  private static final String DB_NAME = "worldreader";
  private static final int DB_VERSION = 1;

  private static final String PRAGMA_FOREIGN_KEY_SUPPORT = "PRAGMA foreign_keys = ON;";

  @Inject public WorldreaderDbOpenHelper(final Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override public void onCreate(final SQLiteDatabase db) {
    db.execSQL(UsersTable.createTableQuery());
    db.execSQL(UserBooksTable.createTableQuery());
    db.execSQL(UserMilestonesTable.createTableQuery());
    db.execSQL(UserScoreTable.createTableQuery());
    db.execSQL(UserBookLikesTable.createTableQuery());
  }

  @Override public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

  }

  @Override public void onOpen(final SQLiteDatabase db) {
    db.execSQL(PRAGMA_FOREIGN_KEY_SUPPORT);
  }
}
