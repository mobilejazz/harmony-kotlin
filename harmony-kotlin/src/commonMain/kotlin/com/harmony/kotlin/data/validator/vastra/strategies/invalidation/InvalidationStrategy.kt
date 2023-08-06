package com.harmony.kotlin.data.validator.vastra.strategies.invalidation

import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategy
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyEntity
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyResult

data class InvalidationStrategyEntity(val isValid: Boolean) : ValidationStrategyEntity

class InvalidationStrategy : ValidationStrategy {

  override fun <T : ValidationStrategyEntity> isValid(t: T): ValidationStrategyResult {
    when (t) {
      is InvalidationStrategyEntity -> {
        return if (!t.isValid) ValidationStrategyResult.INVALID else ValidationStrategyResult.UNKNOWN
      }
      else -> {
        throw IllegalArgumentException("object != InvalidationStrategyEntity")
      }
    }
  }
}
