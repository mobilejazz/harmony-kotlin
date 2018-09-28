package com.mobilejazz.kotlin.core.repository

import com.mobilejazz.kotlin.core.repository.operation.Operation
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.threading.extensions.Future

class VoidRepository<V> : GetRepository<V>, PutRepository<V>, DeleteRepository {

  override fun get(query: Query, operation: Operation): Future<V> = notSupportedOperation()

  override fun getAll(query: Query, operation: Operation): Future<List<V>> = notSupportedOperation()

  override fun put(query: Query, value: V?, operation: Operation): Future<V> = notSupportedOperation()

  override fun putAll(query: Query, value: List<V>?, operation: Operation): Future<List<V>> = notSupportedOperation()

  override fun delete(query: Query, operation: Operation): Future<Unit> = notSupportedOperation()

  override fun deleteAll(query: Query, operation: Operation): Future<Unit> = notSupportedOperation()
}

class VoidGetRepository<V> : GetRepository<V> {

  override fun get(query: Query, operation: Operation): Future<V> = notSupportedOperation()

  override fun getAll(query: Query, operation: Operation): Future<List<V>> = notSupportedOperation()
}

class VoidPutRepository<V> : PutRepository<V> {
  override fun put(query: Query, value: V?, operation: Operation): Future<V> = notSupportedOperation()

  override fun putAll(query: Query, value: List<V>?, operation: Operation): Future<List<V>> = notSupportedOperation()
}

class VoidDeleteRepository : DeleteRepository {
  override fun delete(query: Query, operation: Operation): Future<Unit> = notSupportedOperation()

  override fun deleteAll(query: Query, operation: Operation): Future<Unit> = notSupportedOperation()
}