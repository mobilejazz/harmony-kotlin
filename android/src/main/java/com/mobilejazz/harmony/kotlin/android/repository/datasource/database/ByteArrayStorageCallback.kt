package com.mobilejazz.harmony.kotlin.android.repository.datasource.database

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper

class ByteArrayStorageCallback(version: Int) : SupportSQLiteOpenHelper.Callback(version) {
  override fun onCreate(db: SupportSQLiteDatabase) {
    db.execSQL("""
      | CREATE TABLE ${BlobTable.TABLE_NAME} (
      |   ${BlobTable.COLUMN_KEY} TEXT NOT NULL PRIMARY KEY,
      |   ${BlobTable.COLUMN_VALUE} BLOB NOT NULL
      | );
    """.trimMargin())
  }

  override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("DROP TABLE IF EXISTS ${BlobTable.TABLE_NAME}")
    onCreate(db)
  }
}

object BlobTable {
  const val TABLE_NAME = "cache"
  const val COLUMN_KEY = "key"
  const val COLUMN_VALUE = "value"
}