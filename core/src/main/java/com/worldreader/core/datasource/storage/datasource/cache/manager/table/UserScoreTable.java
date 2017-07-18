package com.worldreader.core.datasource.storage.datasource.cache.manager.table;

import android.support.annotation.NonNull;

public class UserScoreTable {

  public static final String TABLE = "userscore";

  public static final String COLUMN_USER_ID = "userId";
  public static final String COLUMN_BOOK_ID = "bookId";
  public static final String COLUMN_SCORE_ID = "scoreId";
  public static final String COLUMN_SCORE = "score";
  public static final String COLUMN_PAGES = "pages";
  public static final String COLUMN_SYNCHRONIZED = "sync";
  public static final String COLUMN_CREATED_AT = "createdAt";
  public static final String COLUMN_UPDATED_AT = "updatedAt";

  @NonNull public static String createTableQuery() {
    return "CREATE TABLE "
        + TABLE
        + "("
        + COLUMN_USER_ID
        + " TEXT NOT NULL, "
        + COLUMN_BOOK_ID
        + " TEXT, "
        + COLUMN_SCORE_ID
        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COLUMN_SCORE
        + " INTEGER NOT NULL, "
        + COLUMN_PAGES
        + " INTEGER NOT NULL DEFAULT 0, "
        + COLUMN_SYNCHRONIZED
        + " INTEGER DEFAULT 0, "
        + COLUMN_CREATED_AT
        + " TEXT NOT NULL DEFAULT (STRFTIME('%Y-%m-%dT%H:%M:%SZ', 'now')), "
        + COLUMN_UPDATED_AT
        + " TEXT NOT NULL DEFAULT (STRFTIME('%Y-%m-%dT%H:%M:%SZ', 'now')), "
        + " "
        + "UNIQUE ("
        + COLUMN_USER_ID
        + ", "
        + COLUMN_SCORE_ID
        + ")"
        + " FOREIGN KEY ("
        + COLUMN_USER_ID
        + ") REFERENCES "
        + UsersTable.TABLE
        + "("
        + UsersTable.COLUMN_ID
        + ") ON DELETE CASCADE"
        + ");";
  }

}
