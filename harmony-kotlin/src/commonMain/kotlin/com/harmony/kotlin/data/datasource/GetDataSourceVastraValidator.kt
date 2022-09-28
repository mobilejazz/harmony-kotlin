package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.validator.vastra.ValidationService
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyDataSource
import com.harmony.kotlin.error.DataNotValidException

class GetDataSourceVastraValidator<T : ValidationStrategyDataSource>(
  private val getDataSource: GetDataSource<T>,
  private val validator: ValidationService
) : GetDataSource<T> {

  override suspend fun get(query: Query): T = getDataSource.get(query).let {
    if (!validator.isValid(it)) throw DataNotValidException() else it
  }

  override suspend fun getAll(query: Query): List<T> = getDataSource.getAll(query).let {
    if (!validator.isValid(it)) throw DataNotValidException() else it
  }
}
