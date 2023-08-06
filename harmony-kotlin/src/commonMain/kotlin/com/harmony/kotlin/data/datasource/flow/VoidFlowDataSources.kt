package com.harmony.kotlin.data.datasource.flow

import com.harmony.kotlin.data.query.Query
import kotlinx.coroutines.flow.Flow

class VoidFlowDataSource<V> : FlowGetDataSource<V>, FlowPutDataSource<V>, FlowDeleteDataSource {
  override fun get(query: Query): Flow<V> = throw UnsupportedOperationException()

  override fun put(query: Query, value: V?): Flow<V> = throw UnsupportedOperationException()

  override fun delete(query: Query) = throw UnsupportedOperationException()
}

class VoidFlowGetDataSource<V> : FlowGetDataSource<V> {
  override fun get(query: Query): Flow<V> = throw UnsupportedOperationException()
}

class VoidFlowPutDataSource<V> : FlowPutDataSource<V> {
  override fun put(query: Query, value: V?): Flow<V> = throw UnsupportedOperationException()
}

class VoidFlowDeleteDataSource : FlowDeleteDataSource {
  override fun delete(query: Query) = throw UnsupportedOperationException()
}
