package com.harmony.kotlin.data.datasource.validator

import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyEntity
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyResult
import com.harmony.kotlin.data.validator.vastra.strategy.ThreadValidationStrategy
import kotlin.test.Test
import kotlin.test.assertEquals

class ThreadValidationStrategyTest {
  @Test
  internal fun shouldValidateObject_WhenExecutingInProvidedThread() {
    val anyObject = object : ValidationStrategyEntity {}

    val strategy = ThreadValidationStrategy(Thread.currentThread())

    assertEquals(strategy.isValid(anyObject), ValidationStrategyResult.VALID)
  }

  @Test
  internal fun shouldNotValidateObject_WhenNotExecutingInProvidedThread() {
    val anyObject = object : ValidationStrategyEntity {}

    val strategy = ThreadValidationStrategy(Thread())

    assertEquals(strategy.isValid(anyObject), ValidationStrategyResult.UNKNOWN)
  }
}
