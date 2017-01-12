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
  public static final String COLUMN_SCORE = "score";
  public static final String COLUMN_PAGES_PER_DAY = "pagesPerDay";
  public static final String COLUMN_LOCALE = "locale";
  public static final String COLUMN_FONT_SIZE = "fontSize";
  public static final String COLUMN_GENDER = "gender";
  public static final String COLUMN_AGE = "age";
  public static final String COLUMN_BIRTHDATE = "birthDate";
  public static final String COLUMN_CHILDREN_COUNT = "childrenCount";
  public static final String COLUMN_MIN_CHILD_AGE = "minChildAge";
  public static final String COLUMN_MAX_CHILD_AGE = "maxChildAge";
  public static final String COLUMN_CREATED_AT = "createdAt";
  public static final String COLUMN_UPDATED_AT = "updatedAt";
  public static final String COLUMN_MILESTONES = "milestones";
  public static final String COLUMN_FAVORITE_CATEGORIES = "favoriteCategories";

  public static final Query QUERY_ALL = Query.builder().table(TABLE).build();

  public static final DeleteQuery QUERY_DELETE_ALL = DeleteQuery.builder().table(TABLE).build();

  private UsersTable() {
    throw new IllegalStateException("No instances allowed!");
  }

  @NonNull public static String createTableQuery() {
    return "CREATE TABLE "
        + TABLE
        + "("
        + COLUMN_ID
        + " STRING NOT NULL PRIMARY KEY, "
        + COLUMN_PROFILE_ID
        + " INTEGER NULL, "
        + COLUMN_READS_TO_KIDS_ID
        + " INTEGER NULL, "
        + COLUMN_USERNAME
        + " STRING, "
        + COLUMN_NAME
        + " STRING, "
        + COLUMN_EMAIL
        + " STRING, "
        + COLUMN_EMAIL_CONFIRMED
        + " INTEGER, "
        + COLUMN_SCORE
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
        + " STRING, "
        + COLUMN_CHILDREN_COUNT
        + " INTEGER, "
        + COLUMN_MIN_CHILD_AGE
        + " INTEGER, "
        + COLUMN_MAX_CHILD_AGE
        + " INTEGER, "
        + COLUMN_CREATED_AT
        + " STRING, "
        + COLUMN_UPDATED_AT
        + " STRING, "
        + COLUMN_MILESTONES
        + " STRING, "
        + COLUMN_FAVORITE_CATEGORIES
        + " STRING"
        + ");";
  }

}
