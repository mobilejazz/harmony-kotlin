package com.mobilejazz.harmony.kotlin.core.repository.datasource.flow

import com.mobilejazz.harmony.kotlin.core.repository.error.ObjectNotValidException
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.ValidationService
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyDataSource
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FlowDataSourceVastraValidator<T : ValidationStrategyDataSource> @Inject constructor(private val getDataSource: FlowGetDataSource<T>,
                                                                                          private val putDataSource: FlowPutDataSource<T>,
                                                                                          private val deleteDataSource: FlowDeleteDataSource,
                                                                                          private val validator: ValidationService) : FlowGetDataSource<T>, FlowPutDataSource<T>, FlowDeleteDataSource {

  override fun get(query: Query) = getDataSource.get(query).map {
    if (!validator.isValid(it)) throw ObjectNotValidException() else it
  }

  override fun getAll(query: Query) = getDataSource.getAll(query).map {
    if (!validator.isValid(it)) throw ObjectNotValidException() else it
  }

  override fun put(query: Query, value: T?) = putDataSource.put(query, value)

  override fun putAll(query: Query, value: List<T>?) = putDataSource.putAll(query, value)

  override fun delete(query: Query) = deleteDataSource.delete(query)

  override fun deleteAll(query: Query) = deleteDataSource.deleteAll(query)

}