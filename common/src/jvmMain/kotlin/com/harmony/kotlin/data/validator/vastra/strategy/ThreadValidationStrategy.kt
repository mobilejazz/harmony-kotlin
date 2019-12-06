package com.harmony.kotlin.data.validator.vastra.strategy

import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategy
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyDataSource
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyResult
import java.lang.ref.WeakReference

/**
 * This strategy validates any object if the thread in which the validation code run is the thread provided.
 * This is useful for cases when we need synchronous calls and we know that there is already a cached value.
 */
class ThreadValidationStrategy(thread: Thread) : ValidationStrategy {
  private val weakThread = WeakReference(thread)

  override fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult {

    val executingOnValidThread = weakThread.get()?.let {
      it == Thread.currentThread()
    } ?: false

    return if (executingOnValidThread) ValidationStrategyResult.VALID else ValidationStrategyResult.UNKNOWN
  }
}