package com.harmony.kotlin.data.validator.vastra.strategies

enum class ValidationStrategyResult {
  UNKNOWN,
  VALID,
  INVALID
}

interface ValidationStrategyEntity

@Deprecated("Renamed to ValidationStrategyEntity", replaceWith = ReplaceWith("ValidationStrategyEntity"))
typealias ValidationStrategyDataSource = ValidationStrategyEntity

interface ValidationStrategy {

  fun <T : ValidationStrategyEntity> isValid(t: T): ValidationStrategyResult = ValidationStrategyResult.VALID
}
