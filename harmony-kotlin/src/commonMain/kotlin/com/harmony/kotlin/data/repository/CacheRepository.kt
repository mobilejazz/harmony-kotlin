package com.harmony.kotlin.data.repository

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.operation.CacheOperation
import com.harmony.kotlin.data.operation.CacheSyncOperation
import com.harmony.kotlin.data.operation.DefaultOperation
import com.harmony.kotlin.data.operation.MainOperation
import com.harmony.kotlin.data.operation.MainSyncOperation
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.validator.Validator
import com.harmony.kotlin.error.DataNotFoundException
import com.harmony.kotlin.error.DataNotValidException
import com.harmony.kotlin.error.DataSerializationException
import com.harmony.kotlin.error.notSupportedOperation

class CacheRepository<V>(
  private val getCache: GetDataSource<V>,
  private val putCache: PutDataSource<V>,
  private val deleteCache: DeleteDataSource,
  private val getMain: GetDataSource<V>,
  private val putMain: PutDataSource<V>,
  private val deleteMain: DeleteDataSource,
  private val validator: Validator<V> = DefaultValidator()
) : GetRepository<V>, PutRepository<V>, DeleteRepository {

  override suspend fun get(query: Query, operation: Operation): V = when (operation) {
    is DefaultOperation -> get(query, CacheSyncOperation())
    is MainOperation -> getMain.get(query)
    is CacheOperation -> getCache(query, operation)
    is MainSyncOperation -> getMain.get(query).let {
      putCache.put(query, it)
    }

    is CacheSyncOperation -> getCacheSync(query, operation)
    else -> notSupportedOperation()
  }

  @Deprecated("Use get instead")
  override suspend fun getAll(query: Query, operation: Operation): List<V> =
    when (operation) {
      is DefaultOperation -> getAll(query, CacheSyncOperation())
      is MainOperation -> getMain.getAll(query)
      is CacheOperation -> getAllCache(query, operation)
      is MainSyncOperation -> getMain.getAll(query).let { putCache.putAll(query, it) }
      is CacheSyncOperation -> getAllCacheSync(query, operation)
      else -> notSupportedOperation()
    }

  override suspend fun put(query: Query, value: V?, operation: Operation): V = when (operation) {
    is DefaultOperation -> put(query, value, MainSyncOperation)
    is MainOperation -> putMain.put(query, value)
    is CacheOperation -> putCache.put(query, value)
    is MainSyncOperation -> putMain.put(query, value).let { putCache.put(query, it) }
    is CacheSyncOperation -> putCache.put(query, value).let { putMain.put(query, it) }
    else -> notSupportedOperation()
  }

  @Deprecated("Use put instead")
  override suspend fun putAll(query: Query, value: List<V>?, operation: Operation): List<V> = when (operation) {
    is DefaultOperation -> putAll(query, value, MainSyncOperation)
    is MainOperation -> putMain.putAll(query, value)
    is CacheOperation -> putCache.putAll(query, value)
    is MainSyncOperation -> putMain.putAll(query, value).let { putCache.putAll(query, it) }
    is CacheSyncOperation -> putCache.putAll(query, value).let { putMain.putAll(query, it) }
    else -> notSupportedOperation()
  }

  override suspend fun delete(query: Query, operation: Operation): Unit = when (operation) {
    is DefaultOperation -> delete(query, MainSyncOperation)
    is MainOperation -> deleteMain.delete(query)
    is CacheOperation -> deleteCache.delete(query)
    is MainSyncOperation -> deleteMain.delete(query).let { deleteCache.delete(query) }
    is CacheSyncOperation -> deleteCache.delete(query).let { deleteMain.delete(query) }
    else -> notSupportedOperation()
  }

  /**
   *  Default implementation returns always true (all objects are valid)
   */
  class DefaultValidator<T> : Validator<T> {
    override fun isValid(value: T): Boolean {
      return true
    }
  }

  private suspend fun getCache(
    query: Query,
    operation: CacheOperation,
  ): V = try {
    val cacheValue = getCache.get(query)
    if (!validator.isValid(cacheValue)) {
      throw DataNotValidException()
    } else {
      cacheValue
    }
  } catch (cacheException: Exception) {
    if (operation.fallback(cacheException)) {
      getCache.get(query)
    } else {
      throw cacheException
    }
  }

  private suspend fun getAllCache(
    query: Query,
    operation: CacheOperation,
  ): List<V> = try {
    val cacheValues = getCache.getAll(query)
    val invalids = cacheValues.map { validator.isValid(it) }.filter { isValid -> !isValid }
    if (invalids.isNotEmpty()) {
      throw DataNotValidException()
    } else {
      cacheValues
    }
  } catch (cacheException: Exception) {
    if (operation.fallback(cacheException)) {
      getCache.getAll(query)
    } else {
      throw cacheException
    }
  }

  private suspend fun getCacheSync(
    query: Query,
    operation: CacheSyncOperation,
  ): V = try {
    getCache.get(query).let {
      if (!validator.isValid(it)) {
        throw DataNotValidException()
      } else {
        it
      }
    }
  } catch (cacheException: Exception) {
    try {
      when (cacheException) {
        is DataNotValidException,
        is DataSerializationException,
        is DataNotFoundException -> get(query, MainSyncOperation)

        else -> throw cacheException
      }
    } catch (mainException: Exception) {
      if (operation.fallback(mainException, cacheException)) {
        getCache.get(query)
      } else {
        throw mainException
      }
    }
  }

  private suspend fun getAllCacheSync(
    query: Query,
    operation: CacheSyncOperation,
  ): List<V> = try {
    getCache.getAll(query).map {
      if (!validator.isValid(it)) {
        throw DataNotValidException()
      } else {
        it
      }
    }
  } catch (cacheException: Exception) {
    try {
      when (cacheException) {
        is DataNotValidException,
        is DataSerializationException,
        is DataNotFoundException -> getAll(query, MainSyncOperation)

        else -> throw cacheException
      }
    } catch (mainException: Exception) {
      if (operation.fallback(mainException, cacheException)) {
        getCache.getAll(query)
      } else {
        throw mainException
      }
    }
  }
}
