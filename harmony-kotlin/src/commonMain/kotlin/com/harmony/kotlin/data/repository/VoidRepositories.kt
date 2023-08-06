package com.harmony.kotlin.data.repository

import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.error.notSupportedOperation

class VoidRepository<V> : GetRepository<V>, PutRepository<V>, DeleteRepository {

  override suspend fun get(query: Query, operation: Operation): V = notSupportedOperation()

  override suspend fun put(query: Query, value: V?, operation: Operation): V = notSupportedOperation()

  override suspend fun delete(query: Query, operation: Operation) = notSupportedOperation()
}

class VoidGetRepository<V> : GetRepository<V> {

  override suspend fun get(query: Query, operation: Operation): V = notSupportedOperation()
}

class VoidPutRepository<V> : PutRepository<V> {
  override suspend fun put(query: Query, value: V?, operation: Operation): V = notSupportedOperation()
}

class VoidDeleteRepository : DeleteRepository {
  override suspend fun delete(query: Query, operation: Operation) = notSupportedOperation()
}
