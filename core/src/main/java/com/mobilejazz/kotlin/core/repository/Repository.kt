package com.mobilejazz.kotlin.core.repository

import com.mobilejazz.kotlin.core.repository.operation.DefaultOperation
import com.mobilejazz.kotlin.core.repository.operation.Operation
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.threading.extensions.Future

interface Repository {

  fun notSupportedQuery(): Nothing = throw UnsupportedOperationException("Query not supported")

  fun notSupportedOperation(): Nothing = throw IllegalStateException("Operation not defined")
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
  fun deleteAll(query: Query, operation: Operation = DefaultOperation): Future<Unit>
}