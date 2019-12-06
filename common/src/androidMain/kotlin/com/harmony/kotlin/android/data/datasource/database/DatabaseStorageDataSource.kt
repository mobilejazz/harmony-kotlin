package com.harmony.kotlin.android.data.datasource.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.data.query.Query

class DatabaseStorageDataSource(private val db: SupportSQLiteDatabase) : GetDataSource<ByteArray>, PutDataSource<ByteArray>, DeleteDataSource {
  override suspend fun get(query: Query): ByteArray {
    return when (query) {
      is KeyQuery -> {
        val cursor = db.query(
            SupportSQLiteQueryBuilder.builder(BlobTable.TABLE_NAME)
                .selection("${BlobTable.COLUMN_KEY} == ?", arrayOf(query.key))
                .create()
        )
        if (cursor.moveToFirst()) {
          val value = cursor.getBlob(cursor.getColumnIndex(BlobTable.COLUMN_VALUE))
          cursor.close()

          return value
        } else {
          cursor.close()
          throw DataNotFoundException()
        }
      }
      else -> notSupportedQuery()
    }
  }

  override suspend fun getAll(query: Query): List<ByteArray> = throw UnsupportedOperationException("getAll not supported. Use get instead")

  override suspend fun put(query: Query, value: ByteArray?): ByteArray {
    return value?.also { value ->
      when (query) {
        is KeyQuery ->
          db.insert(
              BlobTable.TABLE_NAME,
              SQLiteDatabase.CONFLICT_REPLACE,
              ContentValues().also {
                it.put(BlobTable.COLUMN_KEY, query.key)
                it.put(BlobTable.COLUMN_VALUE, value)
              }
          )
        else -> notSupportedQuery()
      }
    } ?: throw IllegalArgumentException("value must not be null")
  }

  override suspend fun putAll(query: Query, value: List<ByteArray>?): List<ByteArray> = throw UnsupportedOperationException("putAll not supported. Use put instead")

  override suspend fun delete(query: Query) {
    when (query) {
      is KeyQuery -> {
        db.delete(
            BlobTable.TABLE_NAME,
            "${BlobTable.COLUMN_KEY} == ?",
            arrayOf(query.key)
        )
      }
      else -> notSupportedQuery()
    }
  }

  override suspend fun deleteAll(query: Query) {
    // TODO Options for deleteAll:
    // - Not supported (current implementation)
    // - Ignore query and clear database
    // - Use a new KeysQuery(List<String>) and delete all entries with the indicated query
    throw UnsupportedOperationException("deleteAll not supported. Use delete instead")
  }
}