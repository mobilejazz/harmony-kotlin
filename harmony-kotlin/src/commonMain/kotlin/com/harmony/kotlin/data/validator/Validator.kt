package com.harmony.kotlin.data.validator

import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.GetDataSourceValidator
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyEntity
import kotlin.jvm.JvmName

interface Validator<in T> {

  fun isValid(value: T): Boolean
}

/** Validator for list **/
class ListValidator<in T>(private val singleValueValidator: Validator<T>) : Validator<List<T>> {

  /** Validates all object of the list, if one of them is not valid the whole list is not valid **/
  override fun isValid(value: List<T>): Boolean {
    if (value.isEmpty()) return false

    value.forEach { if (!singleValueValidator.isValid(it)) return false }

    return true
  }
}

/**
 * Create a validator for lists
 */
fun <T> Validator<T>.toListValidator(): Validator<List<T>> {
  val singleValueMapper = this
  return ListValidator(singleValueMapper)
}

/**
 * Helper function to create a GetDataSourceValidator from a Validator
 */
fun <T : ValidationStrategyEntity> Validator<T>.toGetDataSourceValidator(getDataSource: GetDataSource<T>): GetDataSourceValidator<T> {
  return GetDataSourceValidator(getDataSource, this)
}

/**
 * Helper function to create a GetDataSourceValidator from a Validator
 */
@JvmName("toGetDataSourceListValidator")
fun <T : ValidationStrategyEntity> Validator<List<T>>.toGetDataSourceValidator(getDataSource: GetDataSource<List<T>>): GetDataSourceValidator<List<T>> {
  return GetDataSourceValidator(getDataSource, this)
}
