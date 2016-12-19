package com.worldreader.core.datasource.storage.datasource.cache.resolver;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import net.sqlcipher.Cursor;

public interface SQLiteResolver<T> {

  T mapFromCursor(@NonNull Cursor cursor);

  ContentValues mapFromObject(@NonNull T element);

  T performGet(@NonNull String key);

  void performPersist(T object);

  void performDelete(@NonNull String key);
}
