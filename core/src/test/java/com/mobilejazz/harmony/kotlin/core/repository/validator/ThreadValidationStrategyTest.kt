package com.mobilejazz.harmony.kotlin.core.repository.validator

import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyDataSource
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyResult
import com.harmony.kotlin.data.validator.vastra.strategy.ThreadValidationStrategy
import org.junit.Assert
import org.junit.Test


class ThreadValidationStrategyTest {
  @Test
  internal fun shouldValidateObject_WhenExecutingInProvidedThread() {
    val anyObject = object : ValidationStrategyDataSource {}

    val strategy = ThreadValidationStrategy(Thread.currentThread())

    Assert.assertEquals(strategy.isValid(anyObject), ValidationStrategyResult.VALID)
  }

  @Test
  internal fun shouldNotValidateObject_WhenNotExecutingInProvidedThread() {
    val anyObject = object : ValidationStrategyDataSource {}

    val strategy = ThreadValidationStrategy(Thread())

    Assert.assertEquals(strategy.isValid(anyObject), ValidationStrategyResult.UNKNOWN)
  }
}