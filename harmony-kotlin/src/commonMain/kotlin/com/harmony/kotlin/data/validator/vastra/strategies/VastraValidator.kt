package com.harmony.kotlin.data.validator.vastra.strategies

import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.GetDataSourceValidator
import com.harmony.kotlin.data.validator.Validator
import com.harmony.kotlin.data.validator.toGetDataSourceValidator
import com.harmony.kotlin.data.validator.toListValidator
import com.harmony.kotlin.data.validator.vastra.ValidationService
import kotlin.jvm.JvmName

/**
 * Validator that uses a ValidationService
 */
class VastraValidator<T : ValidationStrategyEntity>(private val validationService: ValidationService) : Validator<T> {
  override fun isValid(value: T): Boolean {
    return validationService.isValid(value)
  }
}

/**
 * Helper function to create a VastraValidator from the object to be validated
 */
fun <T : ValidationStrategyEntity> T.toVastraValidator(validationService: ValidationService): Validator<T> {
  return VastraValidator(validationService)
}

/**
 * Helper function to create a VastraValidator from a ValidationService
 */
fun <T : ValidationStrategyEntity> ValidationService.toVastraValidator(): Validator<T> {
  return VastraValidator(this)
}

/**
 * Helper function to create a GetDataSourceValidator from a ValidatorService
 */
fun <T : ValidationStrategyEntity> ValidationService.toGetDataSourceValidator(getDataSource: GetDataSource<T>): GetDataSourceValidator<T> {
  return this.toVastraValidator<T>().toGetDataSourceValidator(getDataSource)
}

@JvmName("toGetDataSourceValidatorList")
fun <T : ValidationStrategyEntity> ValidationService.toGetDataSourceValidator(getDataSource: GetDataSource<List<T>>): GetDataSourceValidator<List<T>> {
  return this.toVastraValidator<T>().toListValidator().toGetDataSourceValidator(getDataSource)
}
