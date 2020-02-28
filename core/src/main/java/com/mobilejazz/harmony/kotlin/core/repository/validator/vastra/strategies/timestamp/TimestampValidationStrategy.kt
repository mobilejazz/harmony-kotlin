package com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.timestamp

import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategy
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyDataSource
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyResult
import java.util.*

abstract class TimestampValidationStrategyDataSource(@Transient open var lastUpdate: Date = Date()) :
    ValidationStrategyDataSource {

  abstract fun expiryTime(): Long
}

class TimestampValidationStrategy : ValidationStrategy {

  override fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult {
    when (t) {
      is TimestampValidationStrategyDataSource -> {
        val diff = System.currentTimeMillis() - t.lastUpdate.time

        return if (diff > t.expiryTime())
          ValidationStrategyResult.INVALID
        else
          ValidationStrategyResult.VALID
      }
      else -> {
        throw IllegalArgumentException("object != TimestampValidationStrategyDataSource")
      }
    }
  }
}