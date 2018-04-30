package com.mobilejazz.kotlin.core.datasource

import com.google.common.util.concurrent.ListenableFuture

typealias Future<K> = ListenableFuture<K>

interface Repository {

  fun notSupportedQuery(): Nothing = throw UnsupportedOperationException("Query not supported")

  fun notSupportedOperation(): Nothing = throw IllegalStateException("Operation not defined")
}

// Repositories
interface GetRepository<V> : Repository {
  fun get(query: Query, operation: Operation = DefaultOperation()): Future<V>
  fun getAll(query: Query, operation: Operation = DefaultOperation()): Future<List<V>>
}

interface PutRepository<V> : Repository {
  fun put(query: Query, value: V, operation: Operation = DefaultOperation()): Future<V>
  fun putAll(query: Query, value: List<V> = emptyList(), operation: Operation = DefaultOperation()): Future<List<V>>
}

interface DeleteRepository : Repository {
  fun delete(query: Query, operation: Operation = DefaultOperation()): Future<Void>
  fun deleteAll(query: Query, operation: Operation = DefaultOperation()): Future<Void>
}

interface DataSource {

  fun notSupportedQuery(): Nothing = throw UnsupportedOperationException("Query not supported")
}

// DataSources
interface GetDataSource<V> : DataSource {
  fun get(query: Query): Future<V>
  fun getAll(query: Query): Future<List<V>>
}

interface PutDataSource<V> : DataSource {
  fun put(query: Query, value: V): Future<V>
  fun putAll(query: Query, value: List<V> = emptyList()): Future<List<V>>
}

interface DeleteDataSource : DataSource {
  fun delete(query: Query): Future<Void>
  fun deleteAll(query: Query): Future<Void>
}