package com.mobilejazz.kotlin.core.repository.validator

import com.mobilejazz.kotlin.core.repository.validator.vastra.ValidationServiceManager
import com.mobilejazz.kotlin.core.repository.validator.vastra.strategies.ValidationStrategy
import com.mobilejazz.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyDataSource
import com.mobilejazz.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyResult
import org.junit.Assert
import org.junit.Test


class ValidationServiceTest {

  @Test
  internal fun shouldValidateObjectImmediately_WhenAnyStrategyIsValid_GivenMoreThanOneStrategy() {
    val anyObject = object : ValidationStrategyDataSource {}
    val alwaysValidStrategy = object : ValidationStrategy {
      override fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult {
        return ValidationStrategyResult.VALID
      }
    }
    val alwaysInvalidStrategy = object : ValidationStrategy {
      override fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult {
        return ValidationStrategyResult.INVALID
      }
    }

    val validationServiceManager = ValidationServiceManager(listOf(alwaysValidStrategy, alwaysInvalidStrategy))

    Assert.assertTrue(validationServiceManager.isValid(anyObject))
  }

  @Test
  internal fun shouldInvalidateObjectImmediately_WhenAnyStrategyIsInvalid_GivenMoreThanOneStrategy() {
    val anyObject = object : ValidationStrategyDataSource {}
    val alwaysValidStrategy = object : ValidationStrategy {
      override fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult {
        return ValidationStrategyResult.VALID
      }
    }
    val alwaysInvalidStrategy = object : ValidationStrategy {
      override fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult {
        return ValidationStrategyResult.INVALID
      }
    }

    val validationServiceManager = ValidationServiceManager(listOf(alwaysInvalidStrategy, alwaysValidStrategy))

    Assert.assertFalse(validationServiceManager.isValid(anyObject))
  }

}