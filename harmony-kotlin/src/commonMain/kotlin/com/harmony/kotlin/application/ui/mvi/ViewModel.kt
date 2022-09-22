package com.harmony.kotlin.application.ui.mvi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

/**
 * Base class for all of our ViewModels to inherit from
 */
abstract class ViewModel<out VS : ViewState, in A : Action>(private val dispatcher: CoroutineDispatcher = Dispatchers.Main) :
  LifecycleAwareViewModel(),
  CoroutineScope {

  /**
   * Observable state of the view
   */
  abstract val viewState: StateFlow<VS>

  /**
   * Notify the ViewModel of an Action that occurred on the View
   */
  abstract fun onAction(action: A)

  override val coroutineContext: CoroutineContext
    get() = job + dispatcher

  private val job = Job()

  /**
   * Method that wil execute [onChange] **before** each value of the upstream flow is emitted downstream.
   *
   * To be used on Swift to tranform Flow to @Published annotated state.
   */
  fun <T> Flow<T>.observe(onChange: ((T) -> Unit)) {
    onEach {
      onChange(it)
    }.launchIn(
      CoroutineScope(coroutineContext)
    )
  }
}
