package com.worldreader.core.datasource.storage.datasource.cache.manager.table;

import android.support.annotation.NonNull;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

public class UserBooksTable {

  public static final String TABLE = "userbooks";

  public static final String COLUMN_LOCAL_ID = "localId";
  public static final String COLUMN_ID = "id";
  public static final String COLUMN_USER_ID = "userId";
  public static final String COLUMN_BOOK_ID = "bookId";
  public static final String COLUMN_FAVORITE = "favorite";
  public static final String COLUMN_BOOKMARK = "bookmark";
  public static final String COLUMN_FINISHED = "finished";
  public static final String COLUMN_SAVED_OFFLINE_AT = "saveOfflineAt";
  public static final String COLUMN_RATING = "rating";
  public static final String COLUMN_LIKED = "liked";
  public static final String COLUMN_CREATED_AT = "createdAt";
  public static final String COLUMN_UPDATED_AT = "updatedAt";

  public static final Query QUERY_SELECT_ALL = Query.builder().table(TABLE).build();

  public static final DeleteQuery QUERY_DELETE_ALL = DeleteQuery.builder().table(TABLE).build();

  private UserBooksTable() {
    throw new IllegalStateException("No instances allowed!");
  }

  @NonNull public static String createTableQuery() {
    return "CREATE TABLE "
        + TABLE
        + "("
        + COLUMN_ID
        + " INTEGER, "
        + COLUMN_USER_ID
        + " TEXT NOT NULL, "
        + COLUMN_BOOK_ID
        + " TEXT NOT NULL, "
        + COLUMN_FAVORITE
        + " INTEGER DEFAULT 0, "
        + COLUMN_BOOKMARK
        + " TEXT DEFAULT 0, "
        + COLUMN_FINISHED
        + " INTEGER DEFAULT 0, "
        + COLUMN_SAVED_OFFLINE_AT
        + " TEXT, "
        + COLUMN_RATING
        + " INTEGER DEFAULT 0, "
        + COLUMN_LIKED
        + " INTEGER DEFAULT 0, "
        + COLUMN_CREATED_AT
        + " TEXT NOT NULL DEFAULT (STRFTIME('%Y-%m-%dT%H:%M:%SZ', 'now')), "
        + COLUMN_UPDATED_AT
        + " TEXT NOT NULL DEFAULT (STRFTIME('%Y-%m-%dT%H:%M:%SZ', 'now')), "
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
