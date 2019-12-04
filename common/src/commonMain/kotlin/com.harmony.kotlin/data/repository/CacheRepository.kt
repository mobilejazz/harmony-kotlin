package com.harmony.kotlin.data.repository

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.error.ObjectNotValidException
import com.harmony.kotlin.data.operation.*
import com.harmony.kotlin.data.query.Query

class CacheRepository<V>(
        private val getCache: GetDataSource<V>,
        private val putCache: PutDataSource<V>,
        private val deleteCache: DeleteDataSource,
        private val getMain: GetDataSource<V>,
        private val putMain: PutDataSource<V>,
        private val deleteMain: DeleteDataSource
) : GetRepository<V>, PutRepository<V>, DeleteRepository {

  override suspend fun get(query: Query, operation: Operation): V {
    return when (operation) {
      is DefaultOperation -> get(query, CacheSyncOperation)
      is MainOperation -> getMain.get(query)
      is CacheOperation -> getCache.get(query)
      is MainSyncOperation -> getMain.get(query).let { putCache.put(query, it) }
      is CacheSyncOperation -> {
        try {
          return getCache.get(query)
        } catch (e: Exception) {
          when (e) {
            is ObjectNotValidException -> get(query, MainSyncOperation)
            is DataNotFoundException -> get(query, MainSyncOperation)
            else -> throw e
          }
        }
      }
      else -> notSupportedOperation()
    }
  }

  override suspend fun getAll(query: Query, operation: Operation): List<V> {
    return when (operation) {
      is DefaultOperation -> getAll(query, CacheSyncOperation)
      is MainOperation -> getMain.getAll(query)
      is CacheOperation -> getCache.getAll(query)
      is MainSyncOperation -> getMain.getAll(query).let { putCache.putAll(query, it) }
      is CacheSyncOperation -> {
        try {
          return getCache.getAll(query)
        } catch (e: Exception) {
          when (e) {
            is ObjectNotValidException -> getAll(query, MainSyncOperation)
            is DataNotFoundException -> getAll(query, MainSyncOperation)
            else -> throw e
          }
        }
      }
      else -> notSupportedOperation()
    }
  }

  override suspend fun put(query: Query, value: V?, operation: Operation): V = when (operation) {
    is DefaultOperation -> put(query, value, MainSyncOperation)
    is MainOperation -> putMain.put(query, value)
    is CacheOperation -> putCache.put(query, value)
    is MainSyncOperation -> putMain.put(query, value).let { putCache.put(query, it) }
    is CacheSyncOperation -> putCache.put(query, value).let { putMain.put(query, it) }
    else -> notSupportedOperation()
  }

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

  override suspend fun deleteAll(query: Query, operation: Operation): Unit = when (operation) {
    is DefaultOperation -> deleteAll(query, MainSyncOperation)
    is MainOperation -> deleteMain.deleteAll(query)
    is CacheOperation -> deleteCache.deleteAll(query)
    is MainSyncOperation -> deleteMain.deleteAll(query).let { deleteCache.deleteAll(query) }
    is CacheSyncOperation -> deleteCache.deleteAll(query).let { deleteMain.deleteAll(query) }
    else -> notSupportedOperation()
  }
}