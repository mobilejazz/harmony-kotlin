package com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.timestamp

import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategy
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyDataSource
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyResult
import java.util.*

/**
 * Inherit from this class to provide support for expire date validation when using CacheRepository
 *
 * IMPORTANT: When inheriting from this class and creating a new instance parsing from gson you need to manually set the value of lastUpdate field.
 */
abstract class TimestampValidationStrategyDataSource(open var lastUpdate: Date) :
    ValidationStrategyDataSource {

  abstract fun expiryTime(): Long
}

class TimestampValidationStrategy : ValidationStrategy {

  override fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult {
    when (t) {
      is TimestampValidationStrategyDataSource -> {
        val diff = try {
          System.currentTimeMillis() - t.lastUpdate.time
        } catch (e: Exception) {
          // This is a workaround to fix a previous issue that was causing lastUpdate to not be persisted
          // Now if this is detected the cache will be invalidated and the object will be refreshed from main data source.
          return ValidationStrategyResult.INVALID
        }

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