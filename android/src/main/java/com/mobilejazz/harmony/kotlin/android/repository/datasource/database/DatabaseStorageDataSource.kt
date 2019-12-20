package com.mobilejazz.harmony.kotlin.android.repository.datasource.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import com.mobilejazz.harmony.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.harmony.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.harmony.kotlin.core.repository.query.AllObjectsQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.KeyQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future


class DatabaseStorageDataSource(private val db: SupportSQLiteDatabase) : GetDataSource<ByteArray>, PutDataSource<ByteArray>, DeleteDataSource {
  override fun get(query: Query): Future<ByteArray> =
      Future {
        FrameworkSQLiteOpenHelperFactory()
        when (query) {
          is KeyQuery -> {
            val cursor = db.query(
                SupportSQLiteQueryBuilder.builder(BlobTable.TABLE_NAME)
                    .selection("${BlobTable.COLUMN_KEY} == ?", arrayOf(query.key))
                    .create()
            )
            if (cursor.moveToFirst()) {
              val value = cursor.getBlob(cursor.getColumnIndex(BlobTable.COLUMN_VALUE))
              cursor.close()

              return@Future value
            } else {
              cursor.close()
              throw DataNotFoundException()
            }
          }
          else -> notSupportedQuery()
        }
      }

  override fun getAll(query: Query): Future<List<ByteArray>> =
      Future {
        throw UnsupportedOperationException("getAll not supported. Use get instead")
      }


  override fun put(query: Query, value: ByteArray?): Future<ByteArray> =
      Future {
        value?.also { value ->
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

  override fun putAll(query: Query, value: List<ByteArray>?): Future<List<ByteArray>> =
      Future {
        throw UnsupportedOperationException("putAll not supported. Use put instead")
      }

  override fun delete(query: Query): Future<Unit> =
      Future {
        when (query) {
          is AllObjectsQuery -> {
            db.delete(BlobTable.TABLE_NAME, null, null)
            return@Future
          }

          is KeyQuery -> {
            db.delete(
                BlobTable.TABLE_NAME,
                "${BlobTable.COLUMN_KEY} == ?",
                arrayOf(query.key)
            )
            return@Future
          }
          else -> notSupportedQuery()
        }
      }

  override fun deleteAll(query: Query): Future<Unit> =
      Future {
        throw UnsupportedOperationException("deleteAll not supported. Use delete instead")
      }
}