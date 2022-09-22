package com.harmony.kotlin.application.ui.mvi

/**
 * Defines the current state of the View
 */
interface ViewState

/**
 * Returns state [R] from an initial state [T] using [block] lambda.
 * @throws IllegalStateException if the initial state is different than [T]
 */
inline fun <reified T : ViewState, R : ViewState> ViewState.update(block: (state: T) -> R): R {
  check(this is T) { "Unexpected state $this" }
  return block(this)
}
