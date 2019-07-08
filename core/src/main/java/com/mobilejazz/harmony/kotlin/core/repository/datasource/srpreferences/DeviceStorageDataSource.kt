package com.mobilejazz.harmony.kotlin.core.repository.datasource.srpreferences

import android.content.SharedPreferences
import com.mobilejazz.harmony.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.harmony.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.harmony.kotlin.core.repository.query.KeyQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future
import javax.inject.Inject

class DeviceStorageDataSource<T> @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val prefix: String = ""
) : GetDataSource<T>, PutDataSource<T>, DeleteDataSource {

  override fun get(query: Query): Future<T> = Future {
    when (query) {
      is KeyQuery -> {
        val key = addPrefixTo(query.key)
        if (!sharedPreferences.contains(key)) {
          throw DataNotFoundException()
        }

        return@Future sharedPreferences.all[key] as T
      }
      else -> notSupportedQuery()
    }
  }

  override fun getAll(query: Query): Future<List<T>> = Future {
    throw UnsupportedOperationException("getAll not supported. Use get instead")
  }

  override fun put(query: Query, value: T?): Future<T> = Future {
    when (query) {
      is KeyQuery -> {
        return@Future value?.let {
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
  }

  override fun putAll(query: Query, value: List<T>?): Future<List<T>> = Future {
    throw UnsupportedOperationException("putAll not supported. Use put instead")
  }

  override fun delete(query: Query): Future<Unit> = Future {
    when (query) {
      is KeyQuery -> {
        sharedPreferences.edit()
            .remove(addPrefixTo(query.key))
            .apply()
      }
      else -> notSupportedQuery()
    }
  }

  override fun deleteAll(query: Query): Future<Unit> = Future {
    when (query) {
      is KeyQuery -> {
        sharedPreferences.edit()
            .remove(addPrefixTo(query.key))
            .apply()
      }
      else -> notSupportedQuery()
    }
  }

  private fun addPrefixTo(key: String) = if (prefix.isEmpty()) key else "$prefix.$key"
}