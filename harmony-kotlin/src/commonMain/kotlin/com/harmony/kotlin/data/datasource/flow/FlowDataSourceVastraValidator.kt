package com.harmony.kotlin.data.datasource.flow

import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.validator.vastra.ValidationService
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyDataSource
import com.harmony.kotlin.error.DataNotValidException
import kotlinx.coroutines.flow.map

class FlowDataSourceVastraValidator<T : ValidationStrategyDataSource>(
  private val getDataSource: FlowGetDataSource<T>,
  private val putDataSource: FlowPutDataSource<T>,
  private val deleteDataSource: FlowDeleteDataSource,
  private val validator: ValidationService
) : FlowGetDataSource<T>, FlowPutDataSource<T>, FlowDeleteDataSource {

  override fun get(query: Query) = getDataSource.get(query).map {
    if (!validator.isValid(it)) throw DataNotValidException() else it
  }

  override fun getAll(query: Query) = getDataSource.getAll(query).map {
    if (!validator.isValid(it)) throw DataNotValidException() else it
  }

  override fun put(query: Query, value: T?) = putDataSource.put(query, value)

  override fun putAll(query: Query, value: List<T>?) = putDataSource.putAll(query, value)

  override fun delete(query: Query) = deleteDataSource.delete(query)
}
