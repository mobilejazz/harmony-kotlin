package com.mobilejazz.harmony.kotlin.core.repository

import com.mobilejazz.harmony.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.harmony.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.harmony.kotlin.core.repository.error.ObjectNotValidException
import com.mobilejazz.harmony.kotlin.core.repository.operation.*
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.repository.validator.Validator
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future
import com.mobilejazz.harmony.kotlin.core.threading.extensions.flatMap
import com.mobilejazz.harmony.kotlin.core.threading.extensions.map
import com.mobilejazz.harmony.kotlin.core.threading.extensions.recoverWith
import java.io.InvalidClassException
import javax.inject.Inject

class CacheRepository<V> @Inject constructor(
    private val getCache: GetDataSource<V>,
    private val putCache: PutDataSource<V>,
    private val deleteCache: DeleteDataSource,
    private val getMain: GetDataSource<V>,
    private val putMain: PutDataSource<V>,
    private val deleteMain: DeleteDataSource,
    private val validator: Validator<V> = DefaultValidator()
) : GetRepository<V>, PutRepository<V>, DeleteRepository {

  override fun get(
      query: Query,
      operation: Operation
  ): Future<V> = when (operation) {
    is DefaultOperation -> get(query, CacheSyncOperation())
    is MainOperation -> getMain.get(query)
    is CacheOperation -> getCache.get(query).map {
      if (!validator.isValid(it)) {
        throw ObjectNotValidException()
      } else {
        it
      }
    }
    is MainSyncOperation -> getMain.get(query).flatMap { putCache.put(query, it) }
    is CacheSyncOperation ->
      getCache.get(query).map {
        if (!validator.isValid(it)) {
          throw ObjectNotValidException()
        } else {
          it
        }
      }.recoverWith {
        when (it) {
          is ObjectNotValidException -> get(query, MainSyncOperation)
          is DataNotFoundException -> get(query, MainSyncOperation)
          is InvalidClassException -> get(query, MainSyncOperation)
          else -> throw it
        }
      }.recoverWith {
        if (operation.fallback(it)) {
          getCache.get(query)
        } else {
          throw it
        }
      }
  }

  override fun getAll(
      query: Query,
      operation: Operation
  )
      : Future<List<V>> = when (operation) {
    is DefaultOperation -> getAll(query, CacheSyncOperation())
    is MainOperation -> getMain.getAll(query)
    is CacheOperation -> getCache.getAll(query).map {
      if (!validator.isValid(it)) {
        throw ObjectNotValidException()
      } else {
        it
      }
    }.recoverWith {
      if (operation.fallback(it)) {
        getCache.getAll(query)
      } else {
        throw it
      }
    }
    is MainSyncOperation -> getMain.getAll(query).flatMap { putCache.putAll(query, it) }
    is CacheSyncOperation -> {
      getCache.getAll(query).map {
        if (!validator.isValid(it)) {
          throw ObjectNotValidException()
        } else {
          it
        }
      }.recoverWith {
        when (it) {
          is ObjectNotValidException -> getAll(query, MainSyncOperation)
          is DataNotFoundException -> getAll(query, MainSyncOperation)
          is InvalidClassException -> getAll(query, MainSyncOperation)
          else -> throw it
        }
      }.recoverWith {
        if (operation.fallback(it)) {
          getCache.getAll(query)
        } else {
          throw it
        }
      }
    }
  }

  override fun put(
      query: Query,
      value: V?,
      operation: Operation
  )
      : Future<V> = when (operation) {
    is DefaultOperation -> put(query, value, MainSyncOperation)
    is MainOperation -> putMain.put(query, value)
    is CacheOperation -> putCache.put(query, value)
    is MainSyncOperation -> putMain.put(query, value).flatMap { putCache.put(query, it) }
    is CacheSyncOperation -> putCache.put(query, value).flatMap { putMain.put(query, it) }
  }

  override fun putAll(
      query: Query,
      value: List<V>
      ?,
      operation: Operation
  )
      : Future<List<V>> = when (operation) {
    is DefaultOperation -> putAll(query, value, MainSyncOperation)
    is MainOperation -> putMain.putAll(query, value)
    is CacheOperation -> putCache.putAll(query, value)
    is MainSyncOperation -> putMain.putAll(query, value).flatMap { putCache.putAll(query, it) }
    is CacheSyncOperation -> putCache.putAll(query, value).flatMap { putMain.putAll(query, it) }
  }

  override fun delete(
      query: Query,
      operation: Operation
  )
      : Future<Unit> = when (operation) {
    is DefaultOperation -> delete(query, MainSyncOperation)
    is MainOperation -> deleteMain.delete(query)
    is CacheOperation -> deleteCache.delete(query)
    is MainSyncOperation -> deleteMain.delete(query).flatMap { deleteCache.delete(query) }
    is CacheSyncOperation -> deleteCache.delete(query).flatMap { deleteMain.delete(query) }
  }

  override fun deleteAll(
      query: Query,
      operation: Operation
  )
      : Future<Unit> = when (operation) {
    is DefaultOperation -> deleteAll(query, MainSyncOperation)
    is MainOperation -> deleteMain.deleteAll(query)
    is CacheOperation -> deleteCache.deleteAll(query)
    is MainSyncOperation -> deleteMain.deleteAll(query).flatMap { deleteCache.deleteAll(query) }
    is CacheSyncOperation -> deleteCache.deleteAll(query).flatMap { deleteMain.deleteAll(query) }
  }
}

/**
 *  Default implementation returns always true (all objects are valid)
 */
class DefaultValidator<T> : Validator<T> {
  override fun isValid(value: T): Boolean {
    return true
  }
}