package com.mobilejazz.kotlin.core.repository

import com.mobilejazz.kotlin.core.repository.operation.Operation
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.threading.extensions.Future

class VoidRepository<V> : GetRepository<V>, PutRepository<V>, DeleteRepository {


  override fun get(query: Query, operation: Operation): Future<V> {
    throw UnsupportedOperationException()
  }

  override fun getAll(query: Query, operation: Operation): Future<List<V>> {
    throw UnsupportedOperationException()
  }

  override fun put(query: Query, value: V?, operation: Operation): Future<V> {
    throw UnsupportedOperationException()
  }

  override fun putAll(query: Query, value: List<V>?, operation: Operation): Future<List<V>> {
    throw UnsupportedOperationException()
  }

  override fun delete(query: Query, operation: Operation): Future<Void> {
    throw UnsupportedOperationException()
  }

  override fun deleteAll(query: Query, operation: Operation): Future<Void> {
    throw UnsupportedOperationException()
  }
}

class VoidGetRepository<V> : GetRepository<V> {

  override fun get(query: Query, operation: Operation): Future<V> {
    throw UnsupportedOperationException()
  }

  override fun getAll(query: Query, operation: Operation): Future<List<V>> {
    throw UnsupportedOperationException()
  }
}

class VoidPutRepository<V> : PutRepository<V> {
  override fun put(query: Query, value: V?, operation: Operation): Future<V> {
    throw UnsupportedOperationException()
  }

  override fun putAll(query: Query, value: List<V>?, operation: Operation): Future<List<V>> {
    throw UnsupportedOperationException()
  }
}

class VoidDeleteRepository : DeleteRepository {
  override fun delete(query: Query, operation: Operation): Future<Void> {
    throw UnsupportedOperationException()
  }

  override fun deleteAll(query: Query, operation: Operation): Future<Void> {
    throw UnsupportedOperationException()
  }
}