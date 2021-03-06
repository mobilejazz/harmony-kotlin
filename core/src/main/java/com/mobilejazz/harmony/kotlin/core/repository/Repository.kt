package com.mobilejazz.harmony.kotlin.core.repository

import com.mobilejazz.harmony.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.harmony.kotlin.core.repository.operation.DefaultOperation
import com.mobilejazz.harmony.kotlin.core.repository.operation.Operation
import com.mobilejazz.harmony.kotlin.core.repository.query.IdQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.IdsQuery
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future

interface Repository {

  fun notSupportedQuery(): Nothing = throw UnsupportedOperationException("Query not supported")

  fun notSupportedOperation(): Nothing = throw UnsupportedOperationException("Operation not defined")
}

// Repositories
interface GetRepository<V> : Repository {
  fun get(query: Query, operation: Operation = DefaultOperation): Future<V>
  fun getAll(query: Query, operation: Operation = DefaultOperation): Future<List<V>>
}

interface PutRepository<V> : Repository {
  fun put(query: Query, value: V?, operation: Operation = DefaultOperation): Future<V>
  fun putAll(query: Query, value: List<V>? = emptyList(), operation: Operation = DefaultOperation): Future<List<V>>
}

interface DeleteRepository : Repository {
  fun delete(query: Query, operation: Operation = DefaultOperation): Future<Unit>
  @Deprecated("Use delete with AllObjectsQuery to remove all entries or with any other Query to remove one or more entries")
  fun deleteAll(query: Query, operation: Operation = DefaultOperation): Future<Unit>
}

// Extensions

fun <K, V> GetRepository<V>.get(id: K, operation: Operation = DefaultOperation): Future<V> = get(IdQuery(id), operation)

fun <K, V> GetRepository<V>.getAll(ids: List<K>, operation: Operation = DefaultOperation): Future<List<V>> = getAll(IdsQuery(ids), operation)

fun <K, V> PutRepository<V>.put(id: K, value: V?, operation: Operation = DefaultOperation): Future<V> = put(IdQuery(id), value, operation)

fun <K, V> PutRepository<V>.putAll(ids: List<K>, values: List<V>? = emptyList(), operation: Operation = DefaultOperation) = putAll(IdsQuery(ids), values, operation)

fun <K> DeleteRepository.delete(id: K, operation: Operation = DefaultOperation) = delete(IdQuery(id), operation)

fun <K> DeleteRepository.deleteAll(ids: List<K>, operation: Operation = DefaultOperation) = deleteAll(IdsQuery(ids), operation)

fun <K, V> GetRepository<K>.withMapping(mapper: Mapper<K, V>): GetRepository<V> = GetRepositoryMapper(this, mapper)

fun <K, V> PutRepository<K>.withMapping(toMapper: Mapper<K, V>, fromMapper: Mapper<V, K>): PutRepository<V> = PutRepositoryMapper(this, toMapper, fromMapper)