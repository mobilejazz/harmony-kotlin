package com.harmony.kotlin.data.datasource.cache

import com.harmony.core.db.Cache
import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.datasource.database.CacheDatabase
import com.harmony.kotlin.data.query.AllObjectsQuery
import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.error.DataNotFoundException
import com.harmony.kotlin.error.notSupportedQuery

expect interface CacheSQLConfiguration {
  fun provideCacheDatabase(databaseName: String): CacheDatabase
}

class CacheSQLStorageDataSource(private val database: CacheDatabase) : GetDataSource<ByteArray>, PutDataSource<ByteArray>, DeleteDataSource {

  override suspend fun get(query: Query): ByteArray {
    return when (query) {
      is KeyQuery -> {
        database.cacheQueries.value(query.key).executeAsOneOrNull()?.value_ ?: throw DataNotFoundException()
      }
      else -> notSupportedQuery()
    }
  }

  @Deprecated("Use get instead")
  override suspend fun getAll(query: Query): List<ByteArray> {
    val response: List<Cache> = when (query) {
      is AllObjectsQuery -> {
        database.cacheQueries.selectAll().executeAsList()
      }
      is KeyQuery -> {
        database.cacheQueries.value(listSQLQuery(query)).executeAsList()
      }
      else -> notSupportedQuery()
    }
    if (response.isEmpty()) throw DataNotFoundException()

    return response.map { it.value_ }
  }

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

  @Deprecated("Use put instead")
  override suspend fun putAll(query: Query, value: List<ByteArray>?): List<ByteArray> {
    when (query) {
      is KeyQuery -> {
        value?.let {
          database.transaction {
            // delete the current content, because we can't update it as we store it by indexes instead of the whole context
            database.cacheQueries.delete(listSQLQuery(query))
            it.forEachIndexed { idx, raw ->
              database.cacheQueries.insertOrUpdate(listSQLKey(query, idx), raw)
            }
          }
          return it
        } ?: throw IllegalArgumentException("values != null")
      }
      else -> notSupportedQuery()
    }
  }

  override suspend fun delete(query: Query) {
    when (query) {
      is AllObjectsQuery -> {
        database.cacheQueries.deleteAll()
      }
      is KeyQuery -> {
        database.cacheQueries.delete(listSQLQuery(query))
        database.cacheQueries.delete(query.key)
      }
      else -> notSupportedQuery()
    }
  }

  private fun listSQLQuery(query: KeyQuery) = "harmony-generated-${query.key}-%"
  private fun listSQLKey(query: KeyQuery, idx: Int) = "harmony-generated-${query.key}-$idx"
}
