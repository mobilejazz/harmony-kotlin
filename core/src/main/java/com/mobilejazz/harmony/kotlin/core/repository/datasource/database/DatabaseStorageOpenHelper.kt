package com.mobilejazz.harmony.kotlin.core.repository.datasource.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import javax.inject.Inject

class ByteArrayStorageOpenHelper @Inject constructor(
    context: Context,
    dbName: String,
    dbVersion: Int
) : SQLiteOpenHelper(context, dbName, null, dbVersion) {

  override fun onCreate(db: SQLiteDatabase) {
    db.execSQL("""
      | CREATE TABLE ${BlobTable.TABLE_NAME} (
      |   ${BlobTable.COLUMN_KEY} TEXT NOT NULL PRIMARY KEY,
      |   ${BlobTable.COLUMN_VALUE} BLOB NOT NULL
      | );
    """.trimMargin())
  }

  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("DROP TABLE IF EXISTS ${BlobTable.TABLE_NAME}")
    onCreate(db)
  }
}

object BlobTable {
  const val TABLE_NAME = "cache"
  const val COLUMN_KEY = "key"
  const val COLUMN_VALUE = "value"

}
