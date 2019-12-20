package com.mobilejazz.harmony.kotlin.core.repository.datasource.memory

import com.mobilejazz.harmony.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.harmony.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.harmony.kotlin.core.repository.query.AllObjectsQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.IdsQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.KeyQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future
import javax.inject.Inject

class InMemoryDataSource<V> @Inject constructor() : GetDataSource<V>, PutDataSource<V>, DeleteDataSource {

  private val objects: MutableMap<String, V> = mutableMapOf()
  private val arrays: MutableMap<String, List<V>> = mutableMapOf()

  override fun get(query: Query): Future<V> = Future {
    when (query) {
      is KeyQuery -> {
        return@Future objects[query.key].run {
          this ?: throw DataNotFoundException()
        }
      }
      else -> notSupportedQuery()
    }
  }

  override fun getAll(query: Query): Future<List<V>> {
    return Future {
      when (query) {
        is KeyQuery -> {
          return@Future arrays[query.key].run { this ?: throw DataNotFoundException() }
        }
        else -> notSupportedQuery()
      }
    }
  }

  override fun put(query: Query, value: V?): Future<V> = Future {
    when (query) {
      is KeyQuery -> {
        value?.let {
          objects.put(query.key, value).run { value }
        } ?: throw IllegalArgumentException("InMemoryDataSource: value must be not null")
      }
      else -> notSupportedQuery()
    }
  }

  override fun putAll(query: Query, value: List<V>?): Future<List<V>> = Future {
    when (query) {
      is KeyQuery -> {
        value?.let {
          arrays.put(query.key, value).run { value }
        } ?: throw IllegalArgumentException("InMemoryDataSource: values must be not null")

      }
      else -> notSupportedQuery()
    }
  }

  override fun delete(query: Query): Future<Unit> {
    return Future {
      when (query) {
        is AllObjectsQuery -> {
          objects.clear()
          arrays.clear()
          return@Future
        }
        is IdsQuery<*> -> {
          query.identifiers.forEach {
            if (it is String) {
              objects.remove(it)
              arrays.remove(it)
            } else notSupportedQuery()
          }
          return@Future
        }
        is KeyQuery -> {
          objects.remove(query.key)
          arrays.remove(query.key)
          return@Future
        }
        else -> notSupportedQuery()
      }
    }
  }

  override fun deleteAll(query: Query): Future<Unit> = delete(query)
}