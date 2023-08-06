package com.harmony.kotlin.data.validator.vastra.strategies

enum class ValidationStrategyResult {
  UNKNOWN,
  VALID,
  INVALID
}

interface ValidationStrategyEntity

interface ValidationStrategy {

  fun <T : ValidationStrategyEntity> isValid(t: T): ValidationStrategyResult = ValidationStrategyResult.VALID
}
