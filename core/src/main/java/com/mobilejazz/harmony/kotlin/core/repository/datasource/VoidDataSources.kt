package com.mobilejazz.harmony.kotlin.core.repository.datasource

import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future

class VoidDataSource<V> : GetDataSource<V>, PutDataSource<V>, DeleteDataSource {
  override fun get(query: Query): Future<V> = Future { throw UnsupportedOperationException() }

  override fun getAll(query: Query): Future<List<V>> = Future { throw UnsupportedOperationException() }

  override fun put(query: Query, value: V?): Future<V> = Future { throw UnsupportedOperationException() }

  override fun putAll(query: Query, value: List<V>?): Future<List<V>> = Future { throw UnsupportedOperationException() }

  override fun delete(query: Query): Future<Unit> = Future { throw UnsupportedOperationException() }

  override fun deleteAll(query: Query): Future<Unit> = Future { throw UnsupportedOperationException() }
}

class VoidGetDataSource<V> : GetDataSource<V> {
  override fun get(query: Query): Future<V> = Future { throw UnsupportedOperationException() }

  override fun getAll(query: Query): Future<List<V>> = Future { throw UnsupportedOperationException() }
}

class VoidPutDataSource<V> : PutDataSource<V> {
  override fun put(query: Query, value: V?): Future<V> = Future { throw UnsupportedOperationException() }

  override fun putAll(query: Query, value: List<V>?): Future<List<V>> = Future { throw UnsupportedOperationException() }
}

class VoidDeleteDataSource : DeleteDataSource {
  override fun delete(query: Query): Future<Unit> = Future { throw UnsupportedOperationException() }

  override fun deleteAll(query: Query): Future<Unit> = Future { throw UnsupportedOperationException() }
}