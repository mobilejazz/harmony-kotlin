package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.data.mapper.Mapper
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
}

interface PutDataSource<V> {
  suspend fun put(query: Query, value: V?): V
}

interface DeleteDataSource {
  suspend fun delete(query: Query)
}

// Extensions to create
fun <V> GetDataSource<V>.toGetRepository() = SingleGetDataSourceRepository(this)

fun <K, V> GetDataSource<K>.toGetRepository(mapper: Mapper<K, V>): GetRepository<V> = toGetRepository().withMapping(mapper)

fun <V> PutDataSource<V>.toPutRepository() = SinglePutDataSourceRepository(this)

fun <K, V> PutDataSource<K>.toPutRepository(toMapper: Mapper<K, V>, fromMapper: Mapper<V, K>): PutRepository<V> =
  toPutRepository().withMapping(toMapper, fromMapper)

fun DeleteDataSource.toDeleteRepository() = SingleDeleteDataSourceRepository(this)
