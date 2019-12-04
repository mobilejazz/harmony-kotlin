package com.mobilejazz.harmony.kotlin.core.repository.datasource.memory

import com.mobilejazz.harmony.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.harmony.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.harmony.kotlin.core.repository.query.KeyQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.Query

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
            objects.put(query.key, value).run { value }
          } ?: throw IllegalArgumentException("InMemoryDataSource: value must be not null")
        }
        else -> notSupportedQuery()
      }

  override suspend fun putAll(query: Query, value: List<V>?): List<V> =
      when (query) {
        is KeyQuery -> {
          value?.let {
            arrays.put(query.key, value).run { value }
          } ?: throw IllegalArgumentException("InMemoryDataSource: values must be not null")

        }
        else -> notSupportedQuery()
      }

  override suspend fun delete(query: Query) {
    when (query) {
      is KeyQuery -> {
        objects.remove(query.key)
      }
      else -> notSupportedQuery()
    }
  }

  override suspend fun deleteAll(query: Query) {
    when (query) {
      is KeyQuery -> {
        arrays.remove(query.key)
      }
      else -> notSupportedQuery()
    }
  }
}