package com.mobilejazz.harmony.kotlin.core.repository.datasource

import com.mobilejazz.harmony.kotlin.core.repository.*
import com.mobilejazz.harmony.kotlin.core.repository.error.QueryNotSupportedException
import com.mobilejazz.harmony.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.harmony.kotlin.core.repository.query.IdQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.IdsQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.Query

interface DataSource {

  fun notSupportedQuery(): Nothing = throw QueryNotSupportedException("Query not supported")
}

// DataSources
interface GetDataSource<V> : DataSource {
  suspend fun get(query: Query): V

  suspend fun getAll(query: Query): List<V>
}

interface PutDataSource<V> : DataSource {
  suspend fun put(query: Query, value: V?): V

  suspend fun putAll(query: Query, value: List<V>? = emptyList()): List<V>
}

interface DeleteDataSource : DataSource {
  suspend fun delete(query: Query)

  suspend fun deleteAll(query: Query)
}

// Extensions
suspend fun <K, V> GetDataSource<V>.get(id: K): V = get(IdQuery(id))

suspend fun <K, V> GetDataSource<V>.getAll(ids: List<K>): List<V> = getAll(IdsQuery(ids))

suspend fun <K, V> PutDataSource<V>.put(id: K, value: V?): V = put(IdQuery(id), value)

suspend fun <K, V> PutDataSource<V>.putAll(ids: List<K>, values: List<V>?) = putAll(IdsQuery(ids), values)

suspend fun <K> DeleteDataSource.delete(id: K) = delete(IdQuery(id))

suspend fun <K> DeleteDataSource.deleteAll(ids: List<K>) = deleteAll(IdsQuery(ids))

// Extensions to create
fun <V> GetDataSource<V>.toGetRepository() = SingleGetDataSourceRepository(this)

fun <K, V> GetDataSource<K>.toGetRepository(mapper: Mapper<K, V>): GetRepository<V> = toGetRepository().withMapping(mapper)

fun <V> PutDataSource<V>.toPutRepository() = SinglePutDataSourceRepository(this)

fun <K, V> PutDataSource<K>.toPutRepository(toMapper: Mapper<K, V>, fromMapper: Mapper<V, K>): PutRepository<V> = toPutRepository().withMapping(toMapper, fromMapper)

fun DeleteDataSource.toDeleteRepository() = SingleDeleteDataSourceRepository(this)