package com.worldreader.core.datasource.storage.datasource.cache.manager.table;

import android.support.annotation.NonNull;

public class UserMilestonesTable {

  public static final String TABLE = "usermilestones";

  public static final String COLUMN_USER_ID = "userId";
  public static final String COLUMN_MILESTONE_ID = "milestoneId";
  public static final String COLUMN_SCORE = "score";
  public static final String COLUMN_SYNCHRONIZED = "synchronized";
  public static final String COLUMN_CREATED_AT = "createdAt";
  public static final String COLUMN_UPDATED_AT = "updatedAt";

  @NonNull public static String createTableQuery() {
    return "CREATE TABLE "
        + TABLE
        + "("
        + COLUMN_USER_ID
        + " TEXT NOT NULL, "
        + COLUMN_MILESTONE_ID
        + " TEXT NOT NULL, "
        + COLUMN_SCORE
        + " INTEGER NOT NULL, "
        + COLUMN_SYNCHRONIZED
        + " INTEGER DEFAULT 0, "
        + COLUMN_CREATED_AT
        + " TEXT NOT NULL DEFAULT (STRFTIME('%Y-%m-%dT%H:%M:%SZ', 'now')), "
        + COLUMN_UPDATED_AT
        + " TEXT NOT NULL DEFAULT (STRFTIME('%Y-%m-%dT%H:%M:%SZ', 'now')), "
        + " "
        + "PRIMARY KEY ("
        + COLUMN_USER_ID
        + ", "
        + COLUMN_MILESTONE_ID
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
