package com.mobilejazz.harmony.kotlin.core.repository

import com.mobilejazz.harmony.kotlin.core.repository.operation.Operation
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future

class VoidRepository<V> : GetRepository<V>, PutRepository<V>, DeleteRepository {

  override fun get(query: Query, operation: Operation): Future<V> = Future { notSupportedOperation() }

  override fun getAll(query: Query, operation: Operation): Future<List<V>> = Future { notSupportedOperation() }

  override fun put(query: Query, value: V?, operation: Operation): Future<V> = Future { notSupportedOperation() }

  override fun putAll(query: Query, value: List<V>?, operation: Operation): Future<List<V>> = Future { notSupportedOperation() }

  override fun delete(query: Query, operation: Operation): Future<Unit> = Future { notSupportedOperation() }

  override fun deleteAll(query: Query, operation: Operation): Future<Unit> = Future { notSupportedOperation() }
}

class VoidGetRepository<V> : GetRepository<V> {

  override fun get(query: Query, operation: Operation): Future<V> = Future { notSupportedOperation() }

  override fun getAll(query: Query, operation: Operation): Future<List<V>> = Future { notSupportedOperation() }
}

class VoidPutRepository<V> : PutRepository<V> {
  override fun put(query: Query, value: V?, operation: Operation): Future<V> = Future { notSupportedOperation() }

  override fun putAll(query: Query, value: List<V>?, operation: Operation): Future<List<V>> = Future { notSupportedOperation() }
}

class VoidDeleteRepository : DeleteRepository {
  override fun delete(query: Query, operation: Operation): Future<Unit> = Future { notSupportedOperation() }

  override fun deleteAll(query: Query, operation: Operation): Future<Unit> = Future { notSupportedOperation() }
}