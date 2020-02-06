package com.harmony.kotlin.data.datasource.cache

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.datasource.database.CacheDatabase
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.data.query.Query

expect interface CacheSQLConfiguration {
  fun provideCacheDatabase(databaseName: String): CacheDatabase
}

class CacheSQLStorageDataSource(private val database: CacheDatabase) : GetDataSource<ByteArray>, PutDataSource<ByteArray>, DeleteDataSource {

  override suspend fun get(query: Query): ByteArray {
    return when (query) {
      is KeyQuery -> {
        database.cacheQueries.value(query.key).executeAsOneOrNull()?.value ?: throw DataNotFoundException()
      }
      else -> notSupportedQuery()
    }
  }

  override suspend fun getAll(query: Query): List<ByteArray> = throw NotImplementedError("getAll not supported. Use get instead")

  override suspend fun put(query: Query, value: ByteArray?): ByteArray {
    return value?.also {
      when (query) {
        is KeyQuery -> {
          database.cacheQueries.insertOrUpdate(query.key, it)
          return it
        }
        else -> notSupportedQuery()
      }

    } ?: throw IllegalArgumentException("value != null")
  }

  override suspend fun putAll(query: Query, value: List<ByteArray>?): List<ByteArray> = throw NotImplementedError("getAll not supported. Use get instead")

  override suspend fun delete(query: Query) {
    when(query) {
      is KeyQuery -> {
        database.cacheQueries.delete(query.key)
      }
      else -> notSupportedQuery()
    }
  }

  override suspend fun deleteAll(query: Query) = throw NotImplementedError("deleteAll not supported. Use delete instead")

}
