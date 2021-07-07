package com.harmony.kotlin.data.datasource.validator.vastra.strategies.timestamp


import com.harmony.kotlin.common.date.Millis
import com.harmony.kotlin.common.date.Seconds
import com.harmony.kotlin.data.validator.vastra.ValidationServiceManager
import com.harmony.kotlin.data.validator.vastra.strategies.timestamp.TimestampValidationEntity
import com.harmony.kotlin.data.validator.vastra.strategies.timestamp.TimestampValidationStrategy
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime


@ExperimentalTime
class TimestampValidationStrategyTest {
  private data class Foo(val id: String, override val lastUpdatedAt: Millis, override val expireIn: Seconds) : TimestampValidationEntity

  @Test
  internal fun should_value_be_valid_if_value_is_not_expired() {
    val foo = Foo("bar", lastUpdatedAt = Clock.System.now().toEpochMilliseconds(), expireIn = Duration.minutes(1).toLong(DurationUnit.MILLISECONDS))

    val timestampValidationStrategy = TimestampValidationStrategy()
    val validationServiceManager = ValidationServiceManager(listOf(timestampValidationStrategy))

    val isValid = validationServiceManager.isValid(foo)
    assertTrue(actual = isValid)

  }

  @Test
  fun should_value_be_invalid_if_value_is_expired() {
    val yesterday = Clock.System.now().minus(Duration.days(1)).toEpochMilliseconds()
    val foo = Foo("bar", lastUpdatedAt = yesterday, expireIn = Duration.minutes(1).toLong(DurationUnit.MILLISECONDS))

    val timestampValidationStrategy = TimestampValidationStrategy()
    val validationServiceManager = ValidationServiceManager(listOf(timestampValidationStrategy))

    val isValid = validationServiceManager.isValid(foo)

    assertFalse(isValid)
  }
}
