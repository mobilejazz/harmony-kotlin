package com.harmony.kotlin.android.data.datasource

import android.content.SharedPreferences
import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.query.AllObjectsQuery
import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.error.DataNotFoundException
import com.harmony.kotlin.error.notSupportedQuery

class DeviceStorageDataSource<T>(
  private val sharedPreferences: SharedPreferences,
  private val prefix: String = ""
) : GetDataSource<T>, PutDataSource<T>, DeleteDataSource {

  override suspend fun get(query: Query): T =
    when (query) {
      is KeyQuery -> {
        val key = addPrefixTo(query.key)
        if (!sharedPreferences.contains(key)) {
          throw DataNotFoundException()
        }

        sharedPreferences.all[key] as T
      }
      else -> notSupportedQuery()
    }

  override suspend fun put(query: Query, value: T?): T =
    when (query) {
      is KeyQuery -> {
        value?.let {
          val key = addPrefixTo(query.key)
          val editor = sharedPreferences.edit()
          when (value) {
            is String -> editor.putString(key, value).apply()
            is Boolean -> editor.putBoolean(key, value).apply()
            is Float -> editor.putFloat(key, value).apply()
            is Int -> editor.putInt(key, value).apply()
            is Long -> editor.putLong(key, value).apply()
            is Set<*> -> {
              (value as? Set<String>)?.let { castedValue ->
                editor.putStringSet(key, castedValue).apply()
              } ?: throw UnsupportedOperationException("value type is not supported")
            }
            else -> {
              throw UnsupportedOperationException("value type is not supported")
            }
          }

          return@let it
        } ?: throw IllegalArgumentException("${DeviceStorageDataSource::class.java.simpleName}: value must be not null")
      }
      else -> notSupportedQuery()
    }

  override suspend fun delete(query: Query) =
    when (query) {
      is AllObjectsQuery -> {
        with(sharedPreferences.edit()) {
          if (prefix.isNotEmpty()) {
            sharedPreferences.all.keys.filter { it.contains(prefix) }.forEach { remove(it) }
          } else {
            clear()
          }
          apply()
        }
      }
      is KeyQuery -> {
        sharedPreferences.edit()
          .remove(addPrefixTo(query.key))
          .apply()
      }
      else -> notSupportedQuery()
    }

  private fun addPrefixTo(key: String) = if (prefix.isEmpty()) key else "$prefix.$key"
}
