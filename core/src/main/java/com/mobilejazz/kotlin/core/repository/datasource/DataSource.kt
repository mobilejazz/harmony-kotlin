package com.mobilejazz.kotlin.core.repository.datasource

import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.threading.extensions.Future

interface DataSource {

  fun notSupportedQuery(): Nothing = throw UnsupportedOperationException("Query not supported")
}

// DataSources
interface GetDataSource<V> : DataSource {
  fun get(query: Query): Future<V>
  fun getAll(query: Query): Future<List<V>>
}

interface PutDataSource<V> : DataSource {
  fun put(query: Query, value: V?): Future<V>

  fun putAll(query: Query, value: List<V>? = emptyList()): Future<List<V>>
}

interface DeleteDataSource : DataSource {
  fun delete(query: Query): Future<Unit>
  fun deleteAll(query: Query): Future<Unit>
}