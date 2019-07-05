package com.mobilejazz.kotlin.core.repository.validator.vastra

import com.mobilejazz.kotlin.core.repository.validator.vastra.strategies.ValidationStrategy
import com.mobilejazz.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyDataSource
import com.mobilejazz.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyResult

interface ValidationService {

  fun <T : ValidationStrategyDataSource> isValid(t: T?, strategies: List<ValidationStrategy>): Boolean {
    if (t == null) {
      return false
    }

    // Default value set to false;
    var isValid = false

    loop@ for (strategy in strategies) {
      when (strategy.isValid(t)) {
        ValidationStrategyResult.VALID -> {
          isValid = true
          break@loop
        }
        ValidationStrategyResult.INVALID -> {
          isValid = false
          break@loop
        }
        else -> { /*result is ValidationStrategyResult.UNKNOWN, lets iterate to next strategy */
        }
      }
    }

    return isValid
  }

  fun <T : ValidationStrategyDataSource> isValid(values: List<T>, strategies: List<ValidationStrategy>): Boolean {
    if (values.isEmpty()) {
      return false
    }

    for (value in values) {
      if (!isValid(value, strategies)) {
        return false
      }
    }

    return true
  }

  fun <T : ValidationStrategyDataSource> isValid(t: T?): Boolean

  fun <T : ValidationStrategyDataSource> isValid(values: List<T>): Boolean
}


class ValidationServiceManager(private val strategies: List<ValidationStrategy>) : ValidationService {

  override fun <T : ValidationStrategyDataSource> isValid(values: List<T>): Boolean {
    if (values.isEmpty()) {
      return false
    }

    for (value in values) {
      if (!isValid(value)) {
        return false
      }
    }

    return true
  }

  override fun <T : ValidationStrategyDataSource> isValid(t: T?): Boolean {
    return isValid(t, strategies)
  }
}