package com.harmony.kotlin.data.repository.flow

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.operation.DefaultOperation
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.Query
import kotlinx.coroutines.flow.Flow

// Repositories
interface FlowGetRepository<V> {
  fun get(query: Query, operation: Operation = DefaultOperation): Flow<V>

  @Deprecated("Use get instead")
  fun getAll(query: Query, operation: Operation = DefaultOperation): Flow<List<V>>
}

interface FlowPutRepository<V> {
  fun put(query: Query, value: V?, operation: Operation = DefaultOperation): Flow<V>

  @Deprecated("Use put instead")
  fun putAll(query: Query, value: List<V>? = emptyList(), operation: Operation = DefaultOperation): Flow<List<V>>
}

interface FlowDeleteRepository {
  fun delete(query: Query, operation: Operation = DefaultOperation): Flow<Unit>
}

// Extensions
fun <K, V> FlowGetRepository<K>.withMapping(mapper: Mapper<K, V>): FlowGetRepository<V> = FlowGetRepositoryMapper(this, mapper)

fun <K, V> FlowPutRepository<K>.withMapping(toMapper: Mapper<K, V>, fromMapper: Mapper<V, K>): FlowPutRepository<V> = FlowPutRepositoryMapper(
  this, toMapper,
  fromMapper
)
