package com.mobilejazz.harmony.kotlin.core.repository

import com.mobilejazz.harmony.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.harmony.kotlin.core.repository.operation.Operation
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future
import javax.inject.Inject

class SingleDataSourceRepository<T> @Inject constructor(
    private val getDataSource: GetDataSource<T>,
    private val putDataSource: PutDataSource<T>,
    private val deleteDataSource: DeleteDataSource
) : GetRepository<T>, PutRepository<T>, DeleteRepository {

  override fun get(query: Query, operation: Operation): Future<T> = getDataSource.get(query)

  override fun getAll(query: Query, operation: Operation): Future<List<T>> = getDataSource.getAll(query)

  override fun put(query: Query, value: T?, operation: Operation): Future<T> = putDataSource.put(query, value)

  override fun putAll(query: Query, value: List<T>?, operation: Operation): Future<List<T>> = putDataSource.putAll(query, value)

  override fun delete(query: Query, operation: Operation): Future<Unit> = deleteDataSource.delete(query)

  override fun deleteAll(query: Query, operation: Operation): Future<Unit> = deleteDataSource.deleteAll(query)
}

class SingleGetDataSourceRepository<T> @Inject constructor(private val getDataSource: GetDataSource<T>) : GetRepository<T> {

  override fun get(query: Query, operation: Operation): Future<T> = getDataSource.get(query)

  override fun getAll(query: Query, operation: Operation): Future<List<T>> = getDataSource.getAll(query)
}

class SinglePutDataSourceRepository<T> @Inject constructor(private val putDataSource: PutDataSource<T>) : PutRepository<T> {
  override fun put(query: Query, value: T?, operation: Operation): Future<T> = putDataSource.put(query, value)

  override fun putAll(query: Query, value: List<T>?, operation: Operation): Future<List<T>> = putDataSource.putAll(query, value)
}


class SingleDeleteDataSourceRepository @Inject constructor(private val deleteDataSource: DeleteDataSource) : DeleteRepository {

  override fun delete(query: Query, operation: Operation): Future<Unit> = deleteDataSource.delete(query)

  override fun deleteAll(query: Query, operation: Operation): Future<Unit> = deleteDataSource.deleteAll(query)
}