package com.worldreader.core.datasource.storage.datasource.cache.manager.table;

import android.support.annotation.NonNull;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

public class UsersTable {

  public static final String TABLE = "users";

  public static final String COLUMN_ID = "id";
  public static final String COLUMN_PROFILE_ID = "profileId";
  public static final String COLUMN_READS_TO_KIDS_ID = "readToKidsId";
  public static final String COLUMN_USERNAME = "userName";
  public static final String COLUMN_NAME = "name";
  public static final String COLUMN_EMAIL = "email";
  public static final String COLUMN_EMAIL_CONFIRMED = "emailConfirmed";
  public static final String COLUMN_PAGES_PER_DAY = "pagesPerDay";
  public static final String COLUMN_LOCALE = "locale";
  public static final String COLUMN_FONT_SIZE = "fontSize";
  public static final String COLUMN_GENDER = "gender";
  public static final String COLUMN_AGE = "age";
  public static final String COLUMN_BIRTHDATE = "birthDate";
  public static final String COLUMN_CHILDREN_COUNT = "childrenCount";
  public static final String COLUMN_MIN_CHILD_AGE = "minChildAge";
  public static final String COLUMN_MAX_CHILD_AGE = "maxChildAge";
  public static final String COLUMN_PICTURE = "picture";
  public static final String COLUMN_CREATED_AT = "createdAt";
  public static final String COLUMN_UPDATED_AT = "updatedAt";
  public static final String COLUMN_MILESTONES = "milestones";
  public static final String COLUMN_FAVORITE_CATEGORIES = "favoriteCategories";
  public static final String COLUMN_LOCAL_LIBRARY = "localLibrary";
  public static final String COLUMN_CHILD_NAME = "childName";
  public static final String COLUMN_AVATAR_ID = "avatarId";
  public static final String COLUMN_CHILD_BIRTHDATE = "childBirthDate";
  public static final String COLUMN_CHILD_GENDER = "childGender";
  public static final String COLUMN_RELATIONSHIP = "relationship";

  public static final String ANONYMOUS_USER_ID = "1";

  public static final Query QUERY_SELECT_ALL_USERS =
      Query.builder().table(TABLE).orderBy("id DESC").build();

  public static final Query QUERY_SELECT_ANONYMOUS_USER = Query.builder()
      .table(TABLE)
      .where(TABLE + "." + COLUMN_ID + " = ?")
      .whereArgs(ANONYMOUS_USER_ID)
      .build();

  public static final Query QUERY_SELECT_LOGGED_USER = Query.builder()
      .table(TABLE)
      .where(TABLE + "." + COLUMN_ID + " != ?")
      .whereArgs(ANONYMOUS_USER_ID)
      .limit(1)
      .build();

  public static final DeleteQuery DELETE_LOGGED_IN_USER = DeleteQuery.builder()
      .table(TABLE)
      .where(TABLE + "." + COLUMN_ID + " != ?")
      .whereArgs(ANONYMOUS_USER_ID)
      .build();

  public static final DeleteQuery QUERY_DELETE_ANONYMOUS_USER = DeleteQuery.builder()
      .table(TABLE)
      .where(TABLE + "." + COLUMN_ID + " = ?")
      .whereArgs(ANONYMOUS_USER_ID)
      .build();

  public static final DeleteQuery QUERY_DELETE_ALL_USERS =
      DeleteQuery.builder().table(TABLE).build();

  private UsersTable() {
    throw new IllegalStateException("No instances allowed!");
  }

  @NonNull public static String createTableQuery() {
    return "CREATE TABLE IF NOT EXISTS "
        + TABLE
        + "("
        + COLUMN_ID
        + " TEXT NOT NULL PRIMARY KEY DEFAULT '"
        + ANONYMOUS_USER_ID
        + "', "
        + COLUMN_PROFILE_ID
        + " INTEGER, "
        + COLUMN_READS_TO_KIDS_ID
        + " INTEGER, "
        + COLUMN_USERNAME
        + " TEXT, "
        + COLUMN_NAME
        + " TEXT, "
        + COLUMN_EMAIL
        + " TEXT, "
        + COLUMN_EMAIL_CONFIRMED
        + " INTEGER, "
        + COLUMN_PAGES_PER_DAY
        + " INTEGER, "
        + COLUMN_LOCALE
        + " STRING, "
        + COLUMN_FONT_SIZE
        + " INTEGER, "
        + COLUMN_GENDER
        + " INTEGER, "
        + COLUMN_AGE
        + " INTEGER, "
        + COLUMN_BIRTHDATE
        + " TEXT, "
        + COLUMN_CHILDREN_COUNT
        + " INTEGER, "
        + COLUMN_MIN_CHILD_AGE
        + " INTEGER, "
        + COLUMN_MAX_CHILD_AGE
        + " INTEGER, "
        + COLUMN_PICTURE
        + " TEXT, "
        + COLUMN_CREATED_AT
        + " TEXT NOT NULL DEFAULT (STRFTIME('%Y-%m-%dT%H:%M:%SZ', 'now')), "
        + COLUMN_UPDATED_AT
        + " TEXT NOT NULL DEFAULT (STRFTIME('%Y-%m-%dT%H:%M:%SZ', 'now')), "
        + COLUMN_MILESTONES
        + " STRING, "
        + COLUMN_FAVORITE_CATEGORIES
        + " STRING, "
        + COLUMN_LOCAL_LIBRARY
        + " STRING, "
        + COLUMN_CHILD_NAME
        + " TEXT, "
        + COLUMN_AVATAR_ID
        + " TEXT, "
        + COLUMN_CHILD_BIRTHDATE
        + " TEXT, "
        + COLUMN_CHILD_GENDER
        + " INTEGER, "
        + COLUMN_RELATIONSHIP
        + " TEXT"
        + ");";
  }

}
