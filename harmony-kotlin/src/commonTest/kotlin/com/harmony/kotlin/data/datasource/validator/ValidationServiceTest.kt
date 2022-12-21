package com.harmony.kotlin.data.datasource.validator

import com.harmony.kotlin.data.validator.vastra.ValidationServiceManager
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategy
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyEntity
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyResult
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationServiceTest {

  @Test
  internal fun shouldValidateObjectImmediately_WhenAnyStrategyIsValid_GivenMoreThanOneStrategy() {
    val anyObject = object : ValidationStrategyEntity {}
    val alwaysValidStrategy = object : ValidationStrategy {
      override fun <T : ValidationStrategyEntity> isValid(t: T): ValidationStrategyResult {
        return ValidationStrategyResult.VALID
      }
    }
    val alwaysInvalidStrategy = object : ValidationStrategy {
      override fun <T : ValidationStrategyEntity> isValid(t: T): ValidationStrategyResult {
        return ValidationStrategyResult.INVALID
      }
    }

    val validationServiceManager = ValidationServiceManager(listOf(alwaysValidStrategy, alwaysInvalidStrategy))

    assertTrue(validationServiceManager.isValid(anyObject))
  }

  @Test
  internal fun shouldInvalidateObjectImmediately_WhenAnyStrategyIsInvalid_GivenMoreThanOneStrategy() {
    val anyObject = object : ValidationStrategyEntity {}
    val alwaysValidStrategy = object : ValidationStrategy {
      override fun <T : ValidationStrategyEntity> isValid(t: T): ValidationStrategyResult {
        return ValidationStrategyResult.VALID
      }
    }
    val alwaysInvalidStrategy = object : ValidationStrategy {
      override fun <T : ValidationStrategyEntity> isValid(t: T): ValidationStrategyResult {
        return ValidationStrategyResult.INVALID
      }
    }

    val validationServiceManager = ValidationServiceManager(listOf(alwaysInvalidStrategy, alwaysValidStrategy))

    assertFalse(validationServiceManager.isValid(anyObject))
  }
}
