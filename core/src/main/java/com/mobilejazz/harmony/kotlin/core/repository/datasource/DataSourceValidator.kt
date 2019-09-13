package com.mobilejazz.harmony.kotlin.core.repository.datasource

import com.mobilejazz.harmony.kotlin.core.repository.error.ObjectNotValidException
import com.mobilejazz.harmony.kotlin.core.repository.query.Query
import com.mobilejazz.harmony.kotlin.core.repository.validator.Validator

class DataSourceValidator<T>(private val getDataSource: GetDataSource<T>,
                             private val putDataSource: PutDataSource<T>,
                             private val deleteDataSource: DeleteDataSource,
                             private val validator: Validator<T>) : GetDataSource<T>, PutDataSource<T>, DeleteDataSource {

  override suspend fun get(query: Query): T = getDataSource.get(query).let {
    if (!validator.isValid(it)) throw ObjectNotValidException() else it
  }

  override suspend fun getAll(query: Query): List<T> = getDataSource.getAll(query).let {
    if (!validator.isValid(it)) throw ObjectNotValidException() else it
  }

  override suspend fun put(query: Query, value: T?): T = putDataSource.put(query, value)

  override suspend fun putAll(query: Query, value: List<T>?): List<T> = putDataSource.putAll(query, value)

  override suspend fun delete(query: Query): Unit = deleteDataSource.delete(query)

  override suspend fun deleteAll(query: Query): Unit = deleteDataSource.deleteAll(query)
}

