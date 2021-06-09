package com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies

import java.io.Serializable

public enum class ValidationStrategyResult {
  UNKNOWN,
  VALID,
  INVALID
}

interface ValidationStrategyDataSource : Serializable

interface ValidationStrategy {

  fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult = ValidationStrategyResult.VALID
}