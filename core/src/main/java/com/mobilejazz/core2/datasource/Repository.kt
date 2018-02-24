package com.mobilejazz.core2.datasource

import com.google.common.util.concurrent.ListenableFuture

// Repositories
interface GetRepository<in Q : Query, V> {
  fun get(query: Q, operation: Operation = DefaultOperation()): ListenableFuture<V>
  fun getAll(query: Q, operation: Operation = DefaultOperation()): ListenableFuture<List<V>>
}

interface PutRepository<in Q : Query, V> {
  fun put(query: Q, value: V, operation: Operation = DefaultOperation()): ListenableFuture<V>
  fun putAll(query: Q, value: List<V> = emptyList(), operation: Operation = DefaultOperation()): ListenableFuture<List<V>>
}

interface DeleteRepository<in Q : Query, V> {
  fun delete(query: Q, value: V, operation: Operation = DefaultOperation()): ListenableFuture<V>
  fun deleteAll(query: Q, values: List<V> = emptyList(), operation: Operation = DefaultOperation()): ListenableFuture<List<V>>
}

// DataSources
interface GetDataSource<in Q : Query, V> {
  fun get(query: Q): ListenableFuture<V>
  fun getAll(query: Q): ListenableFuture<List<V>>
}

interface PutDataSource<V> {
  fun put(query: Query, value: V): ListenableFuture<V>
  fun putAll(query: Query, value: List<V> = emptyList()): ListenableFuture<List<V>>
}

interface DeleteDataSource<in Q : Query, V> {
  fun delete(query: Q, value: V): ListenableFuture<V>
  fun deleteAll(query: Q, values: List<V> = emptyList()): ListenableFuture<List<V>>
}


// Operations

sealed class Operation

class DefaultOperation : Operation()

class NetworkOperation : Operation()
class StorageOperation : Operation()
class CacheOperation : Operation()

// Queries

open class Query

// Errors

sealed class RepositoryError(override val message: String?, override val cause: Throwable?) : Throwable(message)

class ModelNotFoundException(message: String? = "Model not found", cause: Throwable? = null) : RepositoryError(message, cause)
class DeleteModelFailException(message: String? = "Fail deleting model", cause: Throwable? = null) : RepositoryError(message, cause)
class PutModelFailException(message: String? = "Fail updating or inserting model", cause: Throwable? = null) : RepositoryError(message, cause)

@Suppress("FunctionName")
fun NotImplemented(): Nothing = throw NotImplementedError("An operation is not implemented")

@Suppress("FunctionName")
fun QueryNotRegistered(): Nothing = throw IllegalArgumentException("Query not registered!")

@Suppress("FunctionName")
fun OperationNotRegistered(): Nothing = throw IllegalArgumentException("Query not registered!")