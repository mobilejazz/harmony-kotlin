package com.harmony.kotlin.application.ui.mvi

/**
 * Represent an event that happens one time.
 */
open class OneShotEvent<T>(private var event: T? = null) {

  fun consume(block: (T) -> Unit) {
    event?.let(block)
    this.event = null
  }

  class Empty<T> : OneShotEvent<T>(null)
}
