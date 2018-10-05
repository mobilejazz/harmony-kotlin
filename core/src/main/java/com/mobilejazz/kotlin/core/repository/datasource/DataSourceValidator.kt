package com.mobilejazz.kotlin.core.repository.datasource

import com.mobilejazz.kotlin.core.repository.error.ObjectNotValidException
import com.mobilejazz.kotlin.core.repository.query.Query
import com.mobilejazz.kotlin.core.repository.validator.Validator
import com.mobilejazz.kotlin.core.threading.extensions.Future
import com.mobilejazz.kotlin.core.threading.extensions.flatMap
import com.mobilejazz.kotlin.core.threading.extensions.toFuture

class DataSourceValidator<T>(private val getDataSource: GetDataSource<T>,
                             private val putDataSource: PutDataSource<T>,
                             private val deleteDataSource: DeleteDataSource,
                             private val validator: Validator<T>) : GetDataSource<T>, PutDataSource<T>, DeleteDataSource {

  override fun get(query: Query): Future<T> = getDataSource.get(query).flatMap {
    return@flatMap if (!validator.isValid(it)) throw ObjectNotValidException() else it.toFuture()
  }

  override fun getAll(query: Query): Future<List<T>> = getDataSource.getAll(query).flatMap {
    return@flatMap if (!validator.isValid(it)) throw ObjectNotValidException() else it.toFuture()
  }

  override fun put(query: Query, value: T?): Future<T> = putDataSource.put(query, value)

  override fun putAll(query: Query, value: List<T>?): Future<List<T>> = putDataSource.putAll(query, value)

  override fun delete(query: Query): Future<Unit> = deleteDataSource.delete(query)

  override fun deleteAll(query: Query): Future<Unit> = deleteDataSource.deleteAll(query)

}

