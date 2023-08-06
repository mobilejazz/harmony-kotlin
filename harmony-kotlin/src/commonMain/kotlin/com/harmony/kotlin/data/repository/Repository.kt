package com.harmony.kotlin.data.repository

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.operation.DefaultOperation
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.Query

// Repositories
interface GetRepository<V> {
  suspend fun get(query: Query, operation: Operation = DefaultOperation): V
}

interface PutRepository<V> {
  suspend fun put(query: Query, value: V?, operation: Operation = DefaultOperation): V
}

interface DeleteRepository {
  suspend fun delete(query: Query, operation: Operation = DefaultOperation)
}

// Extensions
fun <K, V> GetRepository<K>.withMapping(mapper: Mapper<K, V>): GetRepository<V> = GetRepositoryMapper(this, mapper)

fun <K, V> PutRepository<K>.withMapping(toMapper: Mapper<K, V>, fromMapper: Mapper<V, K>): PutRepository<V> = PutRepositoryMapper(this, toMapper, fromMapper)
