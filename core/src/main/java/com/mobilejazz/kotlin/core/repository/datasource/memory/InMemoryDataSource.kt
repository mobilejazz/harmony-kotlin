package com.mobilejazz.kotlin.core.repository.datasource.memory

import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.repository.query.StringKeyQuery
import com.mobilejazz.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.kotlin.core.threading.Future
import com.mobilejazz.kotlin.core.threading.emptyFuture
import com.mobilejazz.kotlin.core.threading.toFuture
import javax.inject.Inject

class InMemoryDataSource<T> @Inject constructor() : GetDataSource<T>, PutDataSource<T>, DeleteDataSource {

  private val objects: MutableMap<String, T> = mutableMapOf()
  private val arrays: MutableMap<String, List<T>> = mutableMapOf()

  override fun get(query: Query): Future<T> = when (query) {
    is StringKeyQuery -> {
      objects[query.key].run {
        if (this == null) {
          throw DataNotFoundException()
        }

        this.toFuture()
      }
    }
    else -> notSupportedQuery()
  }

  override fun getAll(query: Query): Future<List<T>> = when (query) {
    is StringKeyQuery -> {
      arrays[query.key]
          .run {
            if (this == null) {
              throw DataNotFoundException()
            }
            this.toFuture()
          }
    }
    else -> notSupportedQuery()
  }

  override fun put(query: Query, value: T?): Future<T> = when (query) {
    is StringKeyQuery -> {
      if (value == null)
        throw IllegalArgumentException("InMemoryDataSource: value must be not null")
      else
        objects.put(query.key, value).run { value.toFuture() }
    }
    else -> notSupportedQuery()
  }

  override fun putAll(query: Query, value: List<T>?): Future<List<T>> = when (query) {
    is StringKeyQuery -> {
      if (value == null)
        throw IllegalArgumentException("InMemoryDataSource: values must be not null")
      else
        arrays.put(query.key, value).run { value.toFuture() }
    }
    else -> notSupportedQuery()
  }

  override fun delete(query: Query): Future<Void> = when (query) {
    is StringKeyQuery -> objects.remove(query.key).run { emptyFuture() }
    else -> notSupportedQuery()
  }

  override fun deleteAll(query: Query): Future<Void> = when (query) {
    is StringKeyQuery -> arrays.remove(query.key).run { emptyFuture() }
    else -> notSupportedQuery()
  }

}