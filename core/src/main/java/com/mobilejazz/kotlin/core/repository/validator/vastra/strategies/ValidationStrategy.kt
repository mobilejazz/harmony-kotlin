package com.mobilejazz.kotlin.core.repository.validator.vastra.strategies

public enum class ValidationStrategyResult {
  UNKNOWN,
  VALID,
  INVALID
}

interface ValidationStrategyDataSource

interface ValidationStrategy {

  fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult = ValidationStrategyResult.VALID
}