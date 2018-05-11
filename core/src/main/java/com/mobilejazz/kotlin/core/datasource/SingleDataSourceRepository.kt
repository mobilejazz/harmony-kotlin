package com.mobilejazz.kotlin.core.datasource

class SingleDataSourceRepository<T>(private val getDataSource: GetDataSource<T>,
                                    private val putDataSource: PutDataSource<T>,
                                    private val deleteDataSource: DeleteDataSource) : GetRepository<T>, PutRepository<T>, DeleteRepository {

  override fun get(query: Query, operation: Operation): Future<T> = getDataSource.get(query)

  override fun getAll(query: Query, operation: Operation): Future<List<T>> = getDataSource.getAll(query)

  override fun put(query: Query, value: T, operation: Operation): Future<T> = putDataSource.put(query, value)

  override fun putAll(query: Query, value: List<T>, operation: Operation): Future<List<T>> = putDataSource.putAll(query, value)

  override fun delete(query: Query, operation: Operation): Future<Void> = deleteDataSource.delete(query)

  override fun deleteAll(query: Query, operation: Operation): Future<Void> = deleteDataSource.deleteAll(query)
}

