package com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.timestamp

import com.harmony.kotlin.data.validator.vastra.strategy.TimestampValidationStrategy
import com.harmony.kotlin.data.validator.vastra.strategy.TimestampValidationStrategyDataSource
import com.harmony.kotlin.ext.Dates
import com.harmony.kotlin.data.validator.vastra.ValidationServiceManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

data class Foo(val id: String, val updatedAt: Date, override var expiryTime: Long) : TimestampValidationStrategyDataSource(lastUpdate = updatedAt, expiryTime = expiryTime)

class TimestampValidationStrategyTest {
    @Test
    internal fun should_value_be_valid_if_value_is_not_expired() {
        val foo = Foo("bar", updatedAt = Date(), expiryTime = TimeUnit.MINUTES.toMillis(1))

        val timestampValidationStrategy = TimestampValidationStrategy()
        val validationServiceManager = ValidationServiceManager(listOf(timestampValidationStrategy))

        val isValid = validationServiceManager.isValid(foo)

        assertThat(isValid).isTrue()
    }

    @Test
    fun should_value_be_invalid_if_value_is_expired() {
        val foo = Foo("bar", updatedAt = Dates.yesterday(), expiryTime = TimeUnit.MINUTES.toMillis(1))

        val timestampValidationStrategy = TimestampValidationStrategy()
        val validationServiceManager = ValidationServiceManager(listOf(timestampValidationStrategy))

        val isValid = validationServiceManager.isValid(foo)

        assertThat(isValid).isFalse()
    }
}
