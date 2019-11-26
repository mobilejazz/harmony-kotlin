package com.mobilejazz.harmony.kotlin.core.repository.flowdatasource

import com.mobilejazz.harmony.kotlin.core.repository.error.ObjectNotValidException
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.repository.validator.Validator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlowDataSourceValidator<T>(private val getDataSource: FlowGetDataSource<T>,
                                 private val putDataSource: FlowPutDataSource<T>,
                                 private val deleteDataSource: FlowDeleteDataSource,
                                 private val validator: Validator<T>) : FlowGetDataSource<T>, FlowPutDataSource<T>, FlowDeleteDataSource {

  override fun get(query: Query): Flow<T> = getDataSource.get(query).map {
    if (!validator.isValid(it)) throw ObjectNotValidException() else it
  }

  override fun getAll(query: Query): Flow<List<T>> = getDataSource.getAll(query).map {
    if (!validator.isValid(it)) throw ObjectNotValidException() else it
  }

  override fun put(query: Query, value: T?): Flow<T> = putDataSource.put(query, value)

  override fun putAll(query: Query, value: List<T>?): Flow<List<T>> = putDataSource.putAll(query, value)

  override fun delete(query: Query): Flow<Unit> = deleteDataSource.delete(query)

  override fun deleteAll(query: Query): Flow<Unit> = deleteDataSource.deleteAll(query)
}