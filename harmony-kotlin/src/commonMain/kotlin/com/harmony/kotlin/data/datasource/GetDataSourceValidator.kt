package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.validator.Validator
import com.harmony.kotlin.error.DataNotValidException

data class GetDataSourceValidator<T>(
  private val getDataSource: GetDataSource<T>,
  private val validator: Validator<T>
) : GetDataSource<T> {

  override suspend fun get(query: Query): T = getDataSource.get(query).let {
    if (!validator.isValid(it)) throw
    DataNotValidException() else it
  }
}
