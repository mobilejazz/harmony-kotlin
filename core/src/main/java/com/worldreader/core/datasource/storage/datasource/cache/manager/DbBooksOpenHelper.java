package com.worldreader.core.datasource.storage.datasource.cache.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.CacheBytesTableMeta;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import javax.inject.Inject;
import java.io.*;

public class DbBooksOpenHelper extends SQLiteOpenHelper {

  private static final String DB_NAME = "worldreader_books_cache";
  private static final int DB_VERSION = 1;

  private final Context context;

  @Inject public DbBooksOpenHelper(@NonNull Context context) {
    super(context, DB_NAME, null, DB_VERSION);
    this.context = context.getApplicationContext();
  }

  @NonNull private static String getCreateCacheByteTableQuery() {
    return "CREATE TABLE "
        + CacheBytesTableMeta.TABLE
        + "("
        + CacheBytesTableMeta.COLUMN_KEY
        + " TEXT NOT NULL PRIMARY KEY, "
        + CacheBytesTableMeta.COLUMN_VALUE
        + " BLOB NOT NULL, "
        + CacheBytesTableMeta.COLUMN_TIMESTAMP
        + " INTEGER NOT NULL"
        + ");";
  }

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL(getCreateCacheByteTableQuery());
  }

  @Override public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    db.execSQL("DROP TABLE IF EXISTS " + CacheBytesTableMeta.TABLE);
    onCreate(db);
  }

  public void removeFaultyDb() {
    close();

    String version = context.getDatabasePath(DB_NAME).getPath();
    File dbPathFile = new File(version);
    if (dbPathFile.exists()) {
      dbPathFile.delete();
    }
  }
}
