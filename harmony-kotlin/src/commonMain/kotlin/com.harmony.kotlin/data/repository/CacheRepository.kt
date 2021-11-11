package com.harmony.kotlin.data.repository

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.error.MappingException
import com.harmony.kotlin.data.error.ObjectNotValidException
import com.harmony.kotlin.data.error.OperationNotAllowedException
import com.harmony.kotlin.data.operation.CacheOperation
import com.harmony.kotlin.data.operation.CacheSyncOperation
import com.harmony.kotlin.data.operation.DefaultOperation
import com.harmony.kotlin.data.operation.MainOperation
import com.harmony.kotlin.data.operation.MainSyncOperation
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.validator.Validator

class CacheRepository<V>(
  private val getCache: GetDataSource<V>,
  private val putCache: PutDataSource<V>,
  private val deleteCache: DeleteDataSource,
  private val getMain: GetDataSource<V>,
  private val putMain: PutDataSource<V>,
  private val deleteMain: DeleteDataSource,
  private val validator: Validator<V> = DefaultValidator()
) : GetRepository<V>, PutRepository<V>, DeleteRepository {

  override suspend fun get(query: Query, operation: Operation): V {
    return when (operation) {
      is DefaultOperation -> get(query, CacheSyncOperation())
      is MainOperation -> getMain.get(query)
      is CacheOperation -> getCache.get(query).let {
        try {
          if (!validator.isValid(it)) {
            throw ObjectNotValidException()
          } else {
            it
          }
        } catch (cacheException: Exception) {
          if (operation.fallback(cacheException)) {
            getCache.get(query)
          } else {
            throw cacheException
          }
        }
      }
      is MainSyncOperation -> getMain.get(query).let {
        putCache.put(query, it)
      }
      is CacheSyncOperation -> {
        // Try cache
        try {
          return getCache.get(query).let {
            if (!validator.isValid(it)) {
              throw ObjectNotValidException()
            } else {
              it
            }
          }
        } catch (cacheException: Exception) {
          // If cache fails, try main data source
          try {
            when (cacheException) {
              is ObjectNotValidException,
              is MappingException,
              is DataNotFoundException -> get(query, MainSyncOperation)
              else -> throw cacheException
            }
          } catch (mainException: Exception) {
            // If main data source fails but operation.fallback == true then try again cache without checking validity
            if (operation.fallback(mainException) && cacheException is ObjectNotValidException) {
              getCache.get(query)
            } else {
              throw mainException
            }
          }
        }
      }
      else -> throw OperationNotAllowedException()
    }
  }

  override suspend fun getAll(query: Query, operation: Operation): List<V> {
    return when (operation) {
      is DefaultOperation -> getAll(query, CacheSyncOperation())
      is MainOperation -> getMain.getAll(query)
      is CacheOperation -> getCache.getAll(query).map {
        try {
          if (!validator.isValid(it)) {
            throw ObjectNotValidException()
          } else {
            it
          }
        } catch (cacheException: Exception) {
          if (operation.fallback(cacheException)) {
            getCache.get(query)
          } else {
            throw cacheException
          }
        }
      }
      is MainSyncOperation -> getMain.getAll(query).let { putCache.putAll(query, it) }
      is CacheSyncOperation -> {
        // Try cache
        try {
          return getCache.getAll(query).map {
            if (!validator.isValid(it)) {
              throw ObjectNotValidException()
            } else {
              it
            }
          }
        } catch (cacheException: Exception) {
          // If cache fails, try main data source
          try {
            when (cacheException) {
              is ObjectNotValidException,
              is MappingException,
              is DataNotFoundException -> getAll(query, MainSyncOperation)
              else -> throw cacheException
            }
          } catch (mainException: Exception) {
            // If main data source fails but operation.fallback == true then try again cache without checking validity
            if (operation.fallback(mainException) && cacheException is ObjectNotValidException) {
              getCache.getAll(query)
            } else {
              throw mainException
            }
          }
        }
      }
      else -> throw OperationNotAllowedException()
    }
  }

  override suspend fun put(query: Query, value: V?, operation: Operation): V = when (operation) {
    is DefaultOperation -> put(query, value, MainSyncOperation)
    is MainOperation -> putMain.put(query, value)
    is CacheOperation -> putCache.put(query, value)
    is MainSyncOperation -> putMain.put(query, value).let { putCache.put(query, it) }
    is CacheSyncOperation -> putCache.put(query, value).let { putMain.put(query, it) }
    else -> throw OperationNotAllowedException()
  }

  override suspend fun putAll(query: Query, value: List<V>?, operation: Operation): List<V> = when (operation) {
    is DefaultOperation -> putAll(query, value, MainSyncOperation)
    is MainOperation -> putMain.putAll(query, value)
    is CacheOperation -> putCache.putAll(query, value)
    is MainSyncOperation -> putMain.putAll(query, value).let { putCache.putAll(query, it) }
    is CacheSyncOperation -> putCache.putAll(query, value).let { putMain.putAll(query, it) }
    else -> throw OperationNotAllowedException()
  }

  override suspend fun delete(query: Query, operation: Operation): Unit = when (operation) {
    is DefaultOperation -> delete(query, MainSyncOperation)
    is MainOperation -> deleteMain.delete(query)
    is CacheOperation -> deleteCache.delete(query)
    is MainSyncOperation -> deleteMain.delete(query).let { deleteCache.delete(query) }
    is CacheSyncOperation -> deleteCache.delete(query).let { deleteMain.delete(query) }
    else -> throw OperationNotAllowedException()
  }

  /**
   *  Default implementation returns always true (all objects are valid)
   */
  class DefaultValidator<T> : Validator<T> {
    override fun isValid(value: T): Boolean {
      return true
    }
  }
}
