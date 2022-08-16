package com.harmony.kotlin.data.datasource.memory

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.query.AllObjectsQuery
import com.harmony.kotlin.data.query.IdsQuery
import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.error.DataNotFoundException
import com.harmony.kotlin.error.notSupportedQuery

class InMemoryDataSource<V> : GetDataSource<V>, PutDataSource<V>, DeleteDataSource {

  private val objects: MutableMap<String, V> = mutableMapOf()
  private val arrays: MutableMap<String, List<V>> = mutableMapOf()

  override suspend fun get(query: Query): V =
    when (query) {
      is KeyQuery -> {
        objects[query.key].run {
          this ?: throw DataNotFoundException()
        }
      }
      else -> notSupportedQuery()
    }

  override suspend fun getAll(query: Query): List<V> =
    when (query) {
      is KeyQuery -> {
        arrays[query.key].run { this ?: throw DataNotFoundException() }
      }
      else -> notSupportedQuery()
    }

  override suspend fun put(query: Query, value: V?): V =
    when (query) {
      is KeyQuery -> {
        value?.let {
          arrays.remove(query.key)
          objects.put(query.key, value).run { value }
        } ?: throw IllegalArgumentException("InMemoryDataSource: value must be not null")
      }
      else -> notSupportedQuery()
    }

  override suspend fun putAll(query: Query, value: List<V>?): List<V> =
    when (query) {
      is KeyQuery -> {
        value?.let {
          objects.remove(query.key)
          arrays.put(query.key, value).run { value }
        } ?: throw IllegalArgumentException("InMemoryDataSource: values must be not null")
      }
      else -> notSupportedQuery()
    }

  override suspend fun delete(query: Query) {
    when (query) {
      is AllObjectsQuery -> {
        objects.clear()
        arrays.clear()
      }
      is IdsQuery<*> -> {
        query.identifiers.forEach {
          if (it is String) {
            clearAll(it)
          } else notSupportedQuery()
        }
      }
      is KeyQuery -> {
        clearAll(query.key)
      }
      else -> notSupportedQuery()
    }
  }

  private fun clearAll(key: String) {
    objects.remove(key)
    arrays.remove(key)
  }
}
