package com.mobilejazz.harmony.kotlin.core.repository.flow

import com.mobilejazz.harmony.kotlin.core.repository.flowdatasource.FlowDeleteDataSource
import com.mobilejazz.harmony.kotlin.core.repository.flowdatasource.FlowGetDataSource
import com.mobilejazz.harmony.kotlin.core.repository.flowdatasource.FlowPutDataSource
import com.mobilejazz.harmony.kotlin.core.repository.operation.Operation
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SingleFlowDataSourceRepository<T> @Inject constructor(
    private val getDataSource: FlowGetDataSource<T>,
    private val putDataSource: FlowPutDataSource<T>,
    private val deleteDataSource: FlowDeleteDataSource
) : FlowGetRepository<T>, FlowPutRepository<T>, FlowDeleteRepository {

  override fun get(query: Query, operation: Operation): Flow<T> = getDataSource.get(query)

  override fun getAll(query: Query, operation: Operation): Flow<List<T>> = getDataSource.getAll(query)

  override fun put(query: Query, value: T?, operation: Operation): Flow<T> = putDataSource.put(query, value)

  override fun putAll(query: Query, value: List<T>?, operation: Operation): Flow<List<T>> = putDataSource.putAll(query, value)

  override fun delete(query: Query, operation: Operation) = deleteDataSource.delete(query)

  override fun deleteAll(query: Query, operation: Operation) = deleteDataSource.deleteAll(query)
}

class SingleFlowGetDataSourceRepository<T> @Inject constructor(private val getDataSource: FlowGetDataSource<T>) : FlowGetRepository<T> {

  override fun get(query: Query, operation: Operation): Flow<T> = getDataSource.get(query)

  override fun getAll(query: Query, operation: Operation): Flow<List<T>> = getDataSource.getAll(query)
}

class SingleFlowPutDataSourceRepository<T> @Inject constructor(private val putDataSource: FlowPutDataSource<T>) : FlowPutRepository<T> {
  override fun put(query: Query, value: T?, operation: Operation): Flow<T> = putDataSource.put(query, value)

  override fun putAll(query: Query, value: List<T>?, operation: Operation): Flow<List<T>> = putDataSource.putAll(query, value)
}


class SingleFlowDeleteDataSourceRepository @Inject constructor(private val deleteDataSource: FlowDeleteDataSource) : FlowDeleteRepository {

  override fun delete(query: Query, operation: Operation) = deleteDataSource.delete(query)

  override fun deleteAll(query: Query, operation: Operation) = deleteDataSource.deleteAll(query)
}