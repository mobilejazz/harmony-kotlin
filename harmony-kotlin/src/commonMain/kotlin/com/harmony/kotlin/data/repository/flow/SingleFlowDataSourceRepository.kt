package com.harmony.kotlin.data.repository.flow

import com.harmony.kotlin.data.datasource.flow.FlowDeleteDataSource
import com.harmony.kotlin.data.datasource.flow.FlowGetDataSource
import com.harmony.kotlin.data.datasource.flow.FlowPutDataSource
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.Query
import kotlinx.coroutines.flow.Flow

class SingleFlowDataSourceRepository<T>(
  private val getDataSource: FlowGetDataSource<T>,
  private val putDataSource: FlowPutDataSource<T>,
  private val deleteDataSource: FlowDeleteDataSource
) : FlowGetRepository<T>, FlowPutRepository<T>, FlowDeleteRepository {

  override fun get(query: Query, operation: Operation): Flow<T> = getDataSource.get(query)

  override fun put(query: Query, value: T?, operation: Operation): Flow<T> = putDataSource.put(query, value)

  override fun delete(query: Query, operation: Operation) = deleteDataSource.delete(query)
}

class SingleFlowGetDataSourceRepository<T>(private val getDataSource: FlowGetDataSource<T>) : FlowGetRepository<T> {

  override fun get(query: Query, operation: Operation): Flow<T> = getDataSource.get(query)
}

class SingleFlowPutDataSourceRepository<T>(private val putDataSource: FlowPutDataSource<T>) : FlowPutRepository<T> {
  override fun put(query: Query, value: T?, operation: Operation): Flow<T> = putDataSource.put(query, value)
}

class SingleFlowDeleteDataSourceRepository(private val deleteDataSource: FlowDeleteDataSource) : FlowDeleteRepository {

  override fun delete(query: Query, operation: Operation) = deleteDataSource.delete(query)
}
