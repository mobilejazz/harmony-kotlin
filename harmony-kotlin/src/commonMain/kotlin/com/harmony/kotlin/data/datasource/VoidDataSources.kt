package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.data.query.Query

class VoidDataSource<V> : GetDataSource<V>, PutDataSource<V>, DeleteDataSource {
  override suspend fun get(query: Query): V = throw UnsupportedOperationException()

  override suspend fun put(query: Query, value: V?): V = throw UnsupportedOperationException()

  override suspend fun delete(query: Query) = throw UnsupportedOperationException()
}

class VoidGetDataSource<V> : GetDataSource<V> {
  override suspend fun get(query: Query): V = throw UnsupportedOperationException()
}

class VoidPutDataSource<V> : PutDataSource<V> {
  override suspend fun put(query: Query, value: V?): V = throw UnsupportedOperationException()
}

class VoidDeleteDataSource : DeleteDataSource {
  override suspend fun delete(query: Query) = throw UnsupportedOperationException()
}
