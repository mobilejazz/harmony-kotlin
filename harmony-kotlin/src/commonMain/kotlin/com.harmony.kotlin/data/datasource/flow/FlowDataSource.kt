package com.harmony.kotlin.data.datasource.flow

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.error.QueryNotSupportedException
import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.query.IdQuery
import com.harmony.kotlin.data.query.IdsQuery
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.repository.flow.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface FlowDataSource {

  fun notSupportedQuery(): Nothing = throw QueryNotSupportedException("Query not supported")
}

// DataSources
interface FlowGetDataSource<V> : FlowDataSource {
  fun get(query: Query): Flow<V>

  fun getAll(query: Query): Flow<List<V>>
}

interface FlowPutDataSource<V> : FlowDataSource {
  fun put(query: Query, value: V?): Flow<V>

  fun putAll(query: Query, value: List<V>? = emptyList()): Flow<List<V>>
}

interface FlowDeleteDataSource : FlowDataSource {
  fun delete(query: Query): Flow<Unit>
}

// Extensions
fun <K, V> FlowGetDataSource<V>.get(id: K): Flow<V> = get(IdQuery(id))

fun <K, V> GetDataSource<V>.get(id: K): Flow<V> = flow { emit(get(IdQuery(id))) }

fun <K, V> FlowGetDataSource<V>.getAll(ids: List<K>): Flow<List<V>> = getAll(IdsQuery(ids))

fun <K, V> GetDataSource<V>.getAll(ids: List<K>): Flow<List<V>> = flow { emit(getAll(IdsQuery(ids))) }

fun <K, V> FlowPutDataSource<V>.put(id: K, value: V?): Flow<V> = put(IdQuery(id), value)

fun <K, V> PutDataSource<V>.put(id: K, value: V?): Flow<V> = flow { emit(put(IdQuery(id), value)) }

fun <K, V> FlowPutDataSource<V>.putAll(ids: List<K>, values: List<V>?) = putAll(IdsQuery(ids), values)

fun <K, V> PutDataSource<V>.putAll(ids: List<K>, values: List<V>?): Flow<List<V>> = flow { emit(putAll(IdsQuery(ids), values)) }

fun <K> FlowDeleteDataSource.delete(id: K): Flow<Unit> = delete(IdQuery(id))

fun <K> DeleteDataSource.delete(id: K): Flow<Unit> = flow { emit(delete(IdQuery(id))) }

fun <K> FlowDeleteDataSource.delete(ids: List<K>): Flow<Unit> = delete(IdsQuery(ids))

fun <K> DeleteDataSource.delete(ids: List<K>): Flow<Unit> = flow { emit(delete(IdsQuery(ids))) }

fun <K> FlowDeleteDataSource.deleteAll(vararg ids: K): Flow<Unit> = delete(IdsQuery(listOf(ids)))

//region Creation

// Extensions to create
fun <V> FlowGetDataSource<V>.toFlowGetRepository() = SingleFlowGetDataSourceRepository(this)

fun <K, V> FlowGetDataSource<K>.toFlowGetRepository(mapper: Mapper<K, V>): FlowGetRepository<V> = toFlowGetRepository().withMapping(mapper)

fun <V> FlowPutDataSource<V>.toPutRepository() = SingleFlowPutDataSourceRepository(this)

fun <K, V> FlowPutDataSource<K>.toPutRepository(toMapper: Mapper<K, V>, fromMapper: Mapper<V, K>): FlowPutRepository<V> = toPutRepository().withMapping(toMapper, fromMapper)

fun FlowDeleteDataSource.toDeleteRepository() = SingleFlowDeleteDataSourceRepository(this)
//endregion
