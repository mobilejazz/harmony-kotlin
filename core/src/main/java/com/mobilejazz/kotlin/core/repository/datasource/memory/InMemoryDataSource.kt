package com.mobilejazz.kotlin.core.repository.datasource.memory

import com.mobilejazz.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.kotlin.core.repository.query.KeyQuery
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.repository.query.asTyped
import com.mobilejazz.kotlin.core.threading.extensions.Future
import javax.inject.Inject

class InMemoryDataSource<V> @Inject constructor() : GetDataSource<V>, PutDataSource<V>, DeleteDataSource {

  private val objects: MutableMap<String, V> = mutableMapOf()
  private val arrays: MutableMap<String, List<V>> = mutableMapOf()

  override fun get(query: Query): Future<V> = Future {
    when (query) {
      is KeyQuery<*> -> {
        val keyTyped = query.asTyped<String>()

        return@Future keyTyped?.let {
          objects[it.key].run {
            this ?: throw DataNotFoundException()
          }
        } ?: notSupportedQuery()
      }
      else -> notSupportedQuery()
    }
  }

  override fun getAll(query: Query): Future<List<V>> {
    return Future {
      when (query) {
        is KeyQuery<*> -> {
          val keyTyped = query.asTyped<String>()

          return@Future keyTyped?.let {
            arrays[it.key].run { this ?: throw DataNotFoundException() }
          } ?: notSupportedQuery()
        }
        else -> notSupportedQuery()
      }
    }
  }

  override fun put(query: Query, value: V?): Future<V> = Future {
    when (query) {
      is KeyQuery<*> -> {
        value?.let {
          val keyTyped = query.asTyped<String>()

          keyTyped?.let {
            objects.put(it.key, value).run { value }
          } ?: notSupportedQuery()
        } ?: throw IllegalArgumentException("InMemoryDataSource: value must be not null")
      }
      else -> notSupportedQuery()
    }
  }

  override fun putAll(query: Query, value: List<V>?): Future<List<V>> = Future {
    when (query) {
      is KeyQuery<*> -> {
        value?.let {
          val keyTyped = query.asTyped<String>()

          keyTyped?.let {
            arrays.put(it.key, value).run { value }

          } ?: notSupportedQuery()

        } ?: throw IllegalArgumentException("InMemoryDataSource: values must be not null")

      }
      else -> notSupportedQuery()
    }
  }

  override fun delete(query: Query): Future<Unit> {
    return Future {
      when (query) {
        is KeyQuery<*> -> {
          val keyTyped = query.asTyped<String>()

          keyTyped?.let {
            objects.remove(it.key)
          }

          return@Future
        }
        else -> notSupportedQuery()
      }
    }
  }

  override fun deleteAll(query: Query): Future<Unit> {
    return Future {
      when (query) {
        is KeyQuery<*> -> {
          val keyTyped = query.asTyped<String>()

          keyTyped?.let {
            arrays.remove(it.key)
          }

          return@Future
        }
        else -> notSupportedQuery()
      }
    }
  }
}