package com.worldreader.core.datasource.storage.datasource.cache.manager.table;

import android.support.annotation.NonNull;

public class UserBookLikesTable {

  public static final String TABLE = "userbooklikes";

  public static final String COLUMN_USER_ID = "userId";
  public static final String COLUMN_BOOK_ID = "bookId";
  public static final String COLUMN_LIKED = "liked";
  public static final String COLUMN_SYNCHRONIZED = "sync";
  public static final String COLUMN_LIKED_AT = "likedAt";

  @NonNull public static String createTableQuery() {
    return "CREATE TABLE "
        + TABLE
        + "("
        + COLUMN_BOOK_ID
        + " TEXT, "
        + COLUMN_USER_ID
        + " TEXT, "
        + COLUMN_LIKED
        + " INTEGER, "
        + COLUMN_SYNCHRONIZED
        + " INTEGER DEFAULT 0, "
        + COLUMN_LIKED_AT
        + " TEXT NOT NULL DEFAULT (STRFTIME('%Y-%m-%dT%H:%M:%SZ', 'now')) "
        + ", "
        + " PRIMARY KEY ("
        + COLUMN_USER_ID
        + ", "
        + COLUMN_BOOK_ID
        + ")"
        + " FOREIGN KEY ("
        + COLUMN_USER_ID
        + ") REFERENCES "
        + UsersTable.TABLE
        + "("
        + UsersTable.COLUMN_ID
        + ") ON DELETE CASCADE "
        + ");";
  }

}
