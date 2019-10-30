package com.mobilejazz.harmony.kotlin.core.repository.datasource.flow

import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import kotlinx.coroutines.flow.Flow

class VoidDataSource<V> : FlowGetDataSource<V>, FlowPutDataSource<V>, FlowDeleteDataSource {
  override fun get(query: Query): Flow<V> = throw UnsupportedOperationException()

  override fun getAll(query: Query): Flow<List<V>> = throw UnsupportedOperationException()

  override fun put(query: Query, value: V?): Flow<V> = throw UnsupportedOperationException()

  override fun putAll(query: Query, value: List<V>?): Flow<List<V>> = throw UnsupportedOperationException()

  override fun delete(query: Query) = throw UnsupportedOperationException()

  override fun deleteAll(query: Query) = throw UnsupportedOperationException()
}

class VoidFlowGetDataSource<V> : FlowGetDataSource<V> {
  override fun get(query: Query): Flow<V> = throw UnsupportedOperationException()

  override fun getAll(query: Query): Flow<List<V>> = throw UnsupportedOperationException()
}

class VoidFlowPutDataSource<V> : FlowPutDataSource<V> {
  override fun put(query: Query, value: V?): Flow<V> = throw UnsupportedOperationException()

  override fun putAll(query: Query, value: List<V>?): Flow<List<V>> = throw UnsupportedOperationException()
}

class VoidFlowDeleteDataSource : FlowDeleteDataSource {
  override fun delete(query: Query) = throw UnsupportedOperationException()

  override fun deleteAll(query: Query) = throw UnsupportedOperationException()
}