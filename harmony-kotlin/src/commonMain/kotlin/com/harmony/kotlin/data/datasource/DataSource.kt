package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.query.IdQuery
import com.harmony.kotlin.data.query.IdsQuery
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.repository.GetRepository
import com.harmony.kotlin.data.repository.PutRepository
import com.harmony.kotlin.data.repository.SingleDeleteDataSourceRepository
import com.harmony.kotlin.data.repository.SingleGetDataSourceRepository
import com.harmony.kotlin.data.repository.SinglePutDataSourceRepository
import com.harmony.kotlin.data.repository.withMapping

// DataSources
interface GetDataSource<V> {
  suspend fun get(query: Query): V

  suspend fun getAll(query: Query): List<V>
}

interface PutDataSource<V> {
  suspend fun put(query: Query, value: V?): V

  suspend fun putAll(query: Query, value: List<V>? = emptyList()): List<V>
}

interface DeleteDataSource {
  suspend fun delete(query: Query)
}

// Extensions
suspend fun <K, V> GetDataSource<V>.get(id: K): V = get(IdQuery(id))

suspend fun <K, V> GetDataSource<V>.getAll(ids: List<K>): List<V> = getAll(IdsQuery(ids))

suspend fun <K, V> PutDataSource<V>.put(id: K, value: V?): V = put(IdQuery(id), value)

suspend fun <K, V> PutDataSource<V>.putAll(ids: List<K>, values: List<V>?) = putAll(IdsQuery(ids), values)

suspend fun <K> DeleteDataSource.delete(id: K) = delete(IdQuery(id))

suspend fun <K> DeleteDataSource.delete(ids: List<K>) = delete(IdsQuery(ids))

// Extensions to create
fun <V> GetDataSource<V>.toGetRepository() = SingleGetDataSourceRepository(this)

fun <K, V> GetDataSource<K>.toGetRepository(mapper: Mapper<K, V>): GetRepository<V> = toGetRepository().withMapping(mapper)

fun <V> PutDataSource<V>.toPutRepository() = SinglePutDataSourceRepository(this)

fun <K, V> PutDataSource<K>.toPutRepository(toMapper: Mapper<K, V>, fromMapper: Mapper<V, K>): PutRepository<V> =
  toPutRepository().withMapping(toMapper, fromMapper)

fun DeleteDataSource.toDeleteRepository() = SingleDeleteDataSourceRepository(this)
