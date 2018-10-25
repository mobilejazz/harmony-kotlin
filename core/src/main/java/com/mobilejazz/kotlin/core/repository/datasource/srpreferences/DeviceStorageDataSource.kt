package com.mobilejazz.kotlin.core.repository.datasource.srpreferences

import android.content.SharedPreferences
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.mobilejazz.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.kotlin.core.repository.query.KeyQuery
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.repository.query.StringKeyQuery
import com.mobilejazz.kotlin.core.repository.query.asTyped
import com.mobilejazz.kotlin.core.threading.extensions.Future
import javax.inject.Inject

class StringToModelMapper<out T>(private val clz: Class<T>, private val gson: Gson) : Mapper<String, T> {
  override fun map(from: String): T = gson.fromJson(from, clz)
}

class ModelToStringMapper<in T>(private val gson: Gson) : Mapper<T, String> {
  override fun map(from: T): String = gson.toJson(from)
}

class StringToListModelMapper<out T>(private val gson: Gson) : Mapper<String, List<T>> {
  override fun map(from: String): List<T> {
    return gson.fromJson(from, object : TypeToken<List<T>>() {}.type)
  }
}

class ListModelToStringMapper<in T>(private val gson: Gson) : Mapper<List<T>, String> {
  override fun map(from: List<T>): String = gson.toJson(from)
}

class DeviceStorageDataSource<T> @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val toStringMapper: Mapper<T, String>,
    private val toModelMapper: Mapper<String, T>,
    private val toStringFromListMapper: Mapper<List<T>, String>,
    private val toModelFromListString: Mapper<String, List<T>>
) : GetDataSource<T>, PutDataSource<T>, DeleteDataSource {

  override fun get(query: Query): Future<T> = Future {
    when (query) {
      is KeyQuery<*> -> {
        val keyTyped = query.asTyped<String>()

        return@Future keyTyped?.let {
          if (!sharedPreferences.contains(it.key)) {
            throw DataNotFoundException()
          }

          sharedPreferences.getString(it.key, null)
              .let { toModelMapper.map(it) }
        } ?: notSupportedQuery()
      }
      else -> notSupportedQuery()
    }
  }

  override fun getAll(query: Query): Future<List<T>> {
    return Future {
      when (query) {
        is StringKeyQuery -> {

          if (!sharedPreferences.contains(query.key)) {
            throw DataNotFoundException()
          }

          sharedPreferences.getString(query.key, null)
              .let {
                toModelFromListString.map(it)
              }
        }
        else -> notSupportedQuery()
      }
    }
  }

  @Suppress("USELESS_CAST")
  override fun put(query: Query, value: T?): Future<T> = Future {
    when (query) {
      is StringKeyQuery -> {
        if (value == null) {
          throw IllegalArgumentException("${DeviceStorageDataSource::class.java.simpleName}: value must be not null")
        } else {
          sharedPreferences.edit()
              .putString(query.key, toStringMapper.map(value))
              .apply()
          return@Future value as T
        }
      }
      else -> notSupportedQuery()
    }
  }

  override fun putAll(query: Query, value: List<T>?): Future<List<T>> = Future {
    when (query) {
      is StringKeyQuery -> {
        if (value == null) {
          throw IllegalArgumentException("${DeviceStorageDataSource::class.java.simpleName}: values must be not null")
        } else {
          sharedPreferences.edit()
              .putString(query.key, toStringFromListMapper.map(value))
              .apply()


          return@Future value as List<T>
        }
      }
      else -> notSupportedQuery()
    }
  }

  override fun delete(query: Query): Future<Unit> = Future {
    when (query) {
      is StringKeyQuery -> {
        sharedPreferences.edit()
            .remove(query.key)
            .apply()
      }
      else -> notSupportedQuery()
    }
  }

  override fun deleteAll(query: Query): Future<Unit> = Future {
    when (query) {
      is StringKeyQuery -> {
        sharedPreferences.edit()
            .remove(query.key)
            .apply()
      }
      else -> notSupportedQuery()
    }
  }
}