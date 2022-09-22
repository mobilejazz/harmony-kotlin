package com.harmony.kotlin.data.validator.vastra.strategies

import com.harmony.kotlin.data.validator.Validator
import com.harmony.kotlin.data.validator.vastra.ValidationService

class VastraValidator<T : ValidationStrategyDataSource>(private val validationService: ValidationService) : Validator<T> {
  override fun isValid(value: T): Boolean {
    return validationService.isValid(value)
  }
}

fun <T : ValidationStrategyDataSource> T.toVastraValidator(validationService: ValidationService): Validator<T> {
  return VastraValidator(validationService)
}

fun <T : ValidationStrategyDataSource> ValidationService.toVastraValidator(): Validator<T> {
  return VastraValidator(this)
}
