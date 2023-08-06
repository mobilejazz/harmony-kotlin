package com.harmony.kotlin.data.repository

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.Query

class SingleDataSourceRepository<T>(
  private val getDataSource: GetDataSource<T>,
  private val putDataSource: PutDataSource<T>,
  private val deleteDataSource: DeleteDataSource
) : GetRepository<T>, PutRepository<T>, DeleteRepository {

  override suspend fun get(query: Query, operation: Operation): T = getDataSource.get(query)

  override suspend fun put(query: Query, value: T?, operation: Operation): T = putDataSource.put(query, value)

  override suspend fun delete(query: Query, operation: Operation) = deleteDataSource.delete(query)
}

class SingleGetDataSourceRepository<T>(private val getDataSource: GetDataSource<T>) : GetRepository<T> {

  override suspend fun get(query: Query, operation: Operation): T = getDataSource.get(query)
}

class SinglePutDataSourceRepository<T>(private val putDataSource: PutDataSource<T>) : PutRepository<T> {
  override suspend fun put(query: Query, value: T?, operation: Operation): T = putDataSource.put(query, value)
}

class SingleDeleteDataSourceRepository(private val deleteDataSource: DeleteDataSource) : DeleteRepository {

  override suspend fun delete(query: Query, operation: Operation) = deleteDataSource.delete(query)
}
