package com.worldreader.core.datasource.storage.datasource.cache.resolver;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import com.worldreader.core.datasource.storage.datasource.cache.manager.DbBooksOpenHelper;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheBytes;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.CacheBytesTableMeta;
import com.worldreader.core.datasource.storage.security.KeyManager;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

public class CacheBytesSQLiteResolver implements SQLiteResolver<CacheBytes> {

  private final DbBooksOpenHelper sqLiteOpenHelper;
  private final KeyManager keyManager;

  private static String KEY;

  public CacheBytesSQLiteResolver(Context context, DbBooksOpenHelper sqLiteOpenHelper,
      KeyManager keyManager) {
    SQLiteDatabase.loadLibs(context);
    this.sqLiteOpenHelper = sqLiteOpenHelper;
    this.keyManager = keyManager;

    if (!keyManager.existsKey()) {
      createKey();
    }

  }

  @Override public CacheBytes performGet(@NonNull String key) {
    Cursor cursor = obtainReadableDatabase().query(CacheBytesTableMeta.TABLE, null,
        CacheBytesTableMeta.COLUMN_KEY + " == ?", new String[] { key }, null, null, null, null);

    return mapFromCursor(cursor);
  }

  @Override public void performPersist(CacheBytes object) {
    ContentValues contentValues = mapFromObject(object);
    obtainWritableDatabase().insert(CacheBytesTableMeta.TABLE, null, contentValues);
  }

  @Override public void performDelete(@NonNull String key) {
    obtainWritableDatabase().delete(CacheBytesTableMeta.TABLE,
        CacheBytesTableMeta.COLUMN_KEY + " == ?", new String[] { key });
  }

  @Override public CacheBytes mapFromCursor(@NonNull Cursor cursor) {
    if (cursor.isClosed()) {
      return null;
    }

    if (cursor.moveToFirst()) {

      String key = cursor.getString(cursor.getColumnIndex(CacheBytesTableMeta.COLUMN_KEY));
      byte[] value = cursor.getBlob(cursor.getColumnIndex(CacheBytesTableMeta.COLUMN_VALUE));
      long timestamp = cursor.getLong(cursor.getColumnIndex(CacheBytesTableMeta.COLUMN_TIMESTAMP));

      cursor.close();

      return CacheBytes.create(key, value, timestamp);
    } else {
      cursor.close();
      return null;
    }
  }

  @NonNull @Override public ContentValues mapFromObject(@NonNull CacheBytes element) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(CacheBytesTableMeta.COLUMN_KEY, element.getKey());
    contentValues.put(CacheBytesTableMeta.COLUMN_VALUE, element.getValue());
    contentValues.put(CacheBytesTableMeta.COLUMN_TIMESTAMP, element.getTimestamp());
    return contentValues;
  }

  //region Private Methods

  private SQLiteDatabase obtainWritableDatabase() {
    mapKeyToMemoryIfNecessary();
    try {
      return sqLiteOpenHelper.getWritableDatabase(KEY);
    } catch (SQLiteException e) {
      // Proceed with caution. First, the reasoning for this is:
      //
      //   - http://stackoverflow.com/a/25446166
      //   - https://github.com/sqlcipher/android-database-sqlcipher/issues/76
      //
      // As the exception could be whatever cause, we try our best, so we proceed to delete the
      // file of the database directly and try to recreate it.
      //
      // If for whatever reason the exception is another one... well, we can end deleting the
      // database...
      sqLiteOpenHelper.removeFaultyDb();
      return sqLiteOpenHelper.getWritableDatabase(KEY);
    }
  }

  private SQLiteDatabase obtainReadableDatabase() {
    mapKeyToMemoryIfNecessary();
    try {
      return sqLiteOpenHelper.getReadableDatabase(KEY);
    } catch (SQLiteException e) {
      // See obtainWritableDatabase method
      sqLiteOpenHelper.removeFaultyDb();
      return sqLiteOpenHelper.getReadableDatabase(KEY);
    }
  }

  private synchronized void createKey() {
    keyManager.storeKey(keyManager.generateRandomKey());
  }

  private void mapKeyToMemoryIfNecessary() {
    if (KEY == null) {
      synchronized (this) {
        if (KEY == null) {
          KEY = keyManager.retrieveKey();
        }
      }
    }
  }

  //endregion
}
