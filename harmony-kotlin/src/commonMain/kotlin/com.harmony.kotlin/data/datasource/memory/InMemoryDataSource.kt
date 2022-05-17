package com.harmony.kotlin.data.datasource.memory

import co.touchlab.stately.isolate.IsolateState
import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.query.AllObjectsQuery
import com.harmony.kotlin.data.query.IdsQuery
import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.data.query.Query

class InMemoryDataSource<V> : GetDataSource<V>, PutDataSource<V>, DeleteDataSource {

  private val objects: IsolateState<MutableMap<String, V>> = IsolateState { mutableMapOf() }
  private val arrays: IsolateState<MutableMap<String, List<V>>> = IsolateState { mutableMapOf() }

  override suspend fun get(query: Query): V =
    when (query) {
      is KeyQuery -> {
        objects.access {
          it[query.key].run {
            this ?: throw DataNotFoundException()
          }
        }
      }
      else -> notSupportedQuery()
    }

  override suspend fun getAll(query: Query): List<V> =
    when (query) {
      is KeyQuery -> {
        arrays.access {
          it[query.key].run { this ?: throw DataNotFoundException() }
        }
      }
      else -> notSupportedQuery()
    }

  override suspend fun put(query: Query, value: V?): V =
    when (query) {
      is KeyQuery -> {
        value?.let {
          arrays.access {
            it.remove(query.key)
          }
          objects.access {
            it.put(query.key, value).run { value }
          }
        } ?: throw IllegalArgumentException("InMemoryDataSource: value must be not null")
      }
      else -> notSupportedQuery()
    }

  override suspend fun putAll(query: Query, value: List<V>?): List<V> =
    when (query) {
      is KeyQuery -> {
        value?.let {
          objects.access {
            it.remove(query.key)
          }
          arrays.access {
            it.put(query.key, value).run { value }
          }
        } ?: throw IllegalArgumentException("InMemoryDataSource: values must be not null")
      }
      else -> notSupportedQuery()
    }

  override suspend fun delete(query: Query) {
    when (query) {
      is AllObjectsQuery -> {
        objects.access {
          it.clear()
        }
        arrays.access {
          it.clear()
        }
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
    objects.access {
      it.remove(key)
    }
    arrays.access {
      it.remove(key)
    }
  }
}
