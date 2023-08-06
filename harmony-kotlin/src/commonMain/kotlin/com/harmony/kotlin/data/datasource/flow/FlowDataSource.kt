package com.harmony.kotlin.data.datasource.flow

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.repository.flow.FlowGetRepository
import com.harmony.kotlin.data.repository.flow.FlowPutRepository
import com.harmony.kotlin.data.repository.flow.SingleFlowDeleteDataSourceRepository
import com.harmony.kotlin.data.repository.flow.SingleFlowGetDataSourceRepository
import com.harmony.kotlin.data.repository.flow.SingleFlowPutDataSourceRepository
import com.harmony.kotlin.data.repository.flow.withMapping
import kotlinx.coroutines.flow.Flow

// DataSources
interface FlowGetDataSource<V> {
  fun get(query: Query): Flow<V>
}

interface FlowPutDataSource<V> {
  fun put(query: Query, value: V?): Flow<V>
}

interface FlowDeleteDataSource {
  fun delete(query: Query): Flow<Unit>
}

//region Creation

// Extensions to create
fun <V> FlowGetDataSource<V>.toFlowGetRepository() = SingleFlowGetDataSourceRepository(this)

fun <K, V> FlowGetDataSource<K>.toFlowGetRepository(mapper: Mapper<K, V>): FlowGetRepository<V> = toFlowGetRepository().withMapping(mapper)

fun <V> FlowPutDataSource<V>.toPutRepository() = SingleFlowPutDataSourceRepository(this)

fun <K, V> FlowPutDataSource<K>.toPutRepository(toMapper: Mapper<K, V>, fromMapper: Mapper<V, K>): FlowPutRepository<V> =
  toPutRepository().withMapping(toMapper, fromMapper)

fun FlowDeleteDataSource.toDeleteRepository() = SingleFlowDeleteDataSourceRepository(this)
//endregion
