package com.harmony.kotlin.data.validator.mock

import com.harmony.kotlin.data.validator.Validator

class MockValidator<T>(private val forceValidationResponse: Boolean) : Validator<T> {
  override fun isValid(value: T): Boolean = forceValidationResponse
}
