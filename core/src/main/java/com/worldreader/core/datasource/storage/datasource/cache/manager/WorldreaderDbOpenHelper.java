package com.worldreader.core.datasource.storage.datasource.cache.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBookLikesTable;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBooksTable;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserMilestonesTable;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserScoreTable;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UsersTable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class WorldreaderDbOpenHelper extends SQLiteOpenHelper {

  private static final String DB_NAME = "worldreader";
  private static final int DB_VERSION = 2;

  private static final String PRAGMA_FOREIGN_KEY_SUPPORT = "PRAGMA foreign_keys = ON;";
  private final Logger logger;

  private static final String TAG = "DB-Open-Helper";

  @Inject public WorldreaderDbOpenHelper(final Context context, final Logger logger) {
    super(context, DB_NAME, null, DB_VERSION);
    this.logger = logger;
  }

  @Override public void onCreate(final SQLiteDatabase db) {
  }

  @Override
  public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    if (oldVersion == 1 && newVersion == 2) {
      String DEFAULT_AVATAR_ID = "monkey-avatar";
      db.execSQL(
          "ALTER TABLE " + UsersTable.TABLE + " ADD COLUMN " + UsersTable.COLUMN_AVATAR_ID + " TEXT DEFAULT  \"" + DEFAULT_AVATAR_ID + "\""
      );

      db.execSQL(
          "ALTER TABLE " + UsersTable.TABLE + " ADD COLUMN " + UsersTable.COLUMN_CHILD_NAME + " TEXT"
      );

      db.execSQL(
          "ALTER TABLE " + UsersTable.TABLE + " ADD COLUMN " + UsersTable.COLUMN_RELATIONSHIP + " TEXT"
      );
    }

  }

  @Override public void onOpen(final SQLiteDatabase db) {
    final long start = System.nanoTime();
    logger.d(TAG, "onOpen() - Start at " + start);

    db.execSQL(UsersTable.createTableQuery());
    db.execSQL(UserBooksTable.createTableQuery());
    db.execSQL(UserMilestonesTable.createTableQuery());
    db.execSQL(UserScoreTable.createTableQuery());
    db.execSQL(UserBookLikesTable.createTableQuery());

    final long end = System.nanoTime();
    logger.d(TAG, "onOpen() - End at " + end);

    final long diff = end - start;
    logger.d(TAG, "onOpen() - Diff = " + TimeUnit.NANOSECONDS.toMillis(diff));

    db.execSQL(PRAGMA_FOREIGN_KEY_SUPPORT);
  }
}
