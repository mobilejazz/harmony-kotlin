package com.mobilejazz.harmony.kotlin.core.repository.validator.vastra

import com.mobilejazz.harmony.kotlin.core.repository.validator.Validator
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyDataSource

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
