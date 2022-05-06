package com.harmony.kotlin.data.datasource.flow

import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.validator.Validator
import com.harmony.kotlin.error.DataNotValidException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlowDataSourceValidator<T>(
  private val getDataSource: FlowGetDataSource<T>,
  private val putDataSource: FlowPutDataSource<T>,
  private val deleteDataSource: FlowDeleteDataSource,
  private val validator: Validator<T>
) : FlowGetDataSource<T>, FlowPutDataSource<T>, FlowDeleteDataSource {

  override fun get(query: Query): Flow<T> = getDataSource.get(query).map {
    if (!validator.isValid(it)) throw DataNotValidException() else it
  }

  override fun getAll(query: Query): Flow<List<T>> = getDataSource.getAll(query).map {
    if (!validator.isValid(it)) throw DataNotValidException() else it
  }

  override fun put(query: Query, value: T?): Flow<T> = putDataSource.put(query, value)

  override fun putAll(query: Query, value: List<T>?): Flow<List<T>> = putDataSource.putAll(query, value)

  override fun delete(query: Query): Flow<Unit> = deleteDataSource.delete(query)
}
