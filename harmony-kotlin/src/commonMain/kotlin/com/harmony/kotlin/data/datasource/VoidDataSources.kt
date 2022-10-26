package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.data.query.Query

class VoidDataSource<V> : GetDataSource<V>, PutDataSource<V>, DeleteDataSource {
  override suspend fun get(query: Query): V = throw UnsupportedOperationException()

  @Deprecated("Use get instead")
  override suspend fun getAll(query: Query): List<V> = throw UnsupportedOperationException()

  override suspend fun put(query: Query, value: V?): V = throw UnsupportedOperationException()

  @Deprecated("Use put instead")
  override suspend fun putAll(query: Query, value: List<V>?): List<V> = throw UnsupportedOperationException()

  override suspend fun delete(query: Query) = throw UnsupportedOperationException()
}

class VoidGetDataSource<V> : GetDataSource<V> {
  override suspend fun get(query: Query): V = throw UnsupportedOperationException()

  @Deprecated("Use get instead")
  override suspend fun getAll(query: Query): List<V> = throw UnsupportedOperationException()
}

class VoidPutDataSource<V> : PutDataSource<V> {
  override suspend fun put(query: Query, value: V?): V = throw UnsupportedOperationException()

  @Deprecated("Use put instead")
  override suspend fun putAll(query: Query, value: List<V>?): List<V> = throw UnsupportedOperationException()
}

class VoidDeleteDataSource : DeleteDataSource {
  override suspend fun delete(query: Query) = throw UnsupportedOperationException()
}
