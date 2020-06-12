package com.harmony.kotlin.data.validator.vastra.strategies.timestamp

import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategy
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyDataSource
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyResult
import com.soywiz.klock.DateTime
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

interface TimestampValidationEntity: ValidationStrategyDataSource {
  val lastUpdatedAt: Long

  @ExperimentalTime
  val expireIn: Long
    get() = 10.seconds.toLong(DurationUnit.SECONDS)
}

class TimestampValidationStrategy : ValidationStrategy {

  @ExperimentalTime
  override fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult {
    when (t) {
      is TimestampValidationEntity -> {
        val now = DateTime.now().unixMillisLong
        val lastUpdate = DateTime(t.lastUpdatedAt).unixMillisLong

        val diff = now - lastUpdate
        val difSeconds = diff / 1000

        val isValid =  difSeconds < t.expireIn

        return if (isValid)
          ValidationStrategyResult.VALID
        else
          ValidationStrategyResult.INVALID
      }
      else -> {
        throw IllegalArgumentException("object != TimestampValidationStrategyDataSource")
      }
    }
  }
}