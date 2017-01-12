package com.worldreader.core.datasource.storage.datasource.cache.manager.table;

import android.support.annotation.NonNull;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

public class UserBooksTable {

  public static final String TABLE = "userbooks";

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

  public static final Query QUERY_ALL = Query.builder().table(TABLE).build();

  public static final DeleteQuery QUERY_DELETE_ALL = DeleteQuery.builder().table(TABLE).build();

  private UserBooksTable() {
    throw new IllegalStateException("No instances allowed!");
  }

  @NonNull public static String createTableQuery() {
    return "CREATE TABLE "
        + TABLE
        + "("
        + COLUMN_ID
        + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + COLUMN_USER_ID
        + " STRING, "
        + COLUMN_BOOK_ID
        + " STRING, "
        + COLUMN_FAVORITE
        + " INTEGER, "
        + COLUMN_BOOKMARK
        + " STRING, "
        + COLUMN_FINISHED
        + " INTEGER, "
        + COLUMN_SAVED_OFFLINE_AT
        + " LONG, "
        + COLUMN_RATING
        + " INTEGER, "
        + COLUMN_LIKED
        + " INTEGER, "
        + COLUMN_CREATED_AT
        + " LONG, "
        + COLUMN_UPDATED_AT
        + " LONG "
        + ");";
  }

}
