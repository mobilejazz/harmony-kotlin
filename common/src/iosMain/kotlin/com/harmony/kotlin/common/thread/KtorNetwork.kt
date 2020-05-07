package com.harmony.kotlin.common.thread

import co.touchlab.stately.concurrency.GuardedStableRef
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import platform.Foundation.NSThread
import kotlinx.coroutines.Dispatchers

actual suspend fun <R> network(block: suspend () -> R): R = coroutineScope {
  withContext(childContext()) {
    // if (!isMainThread) error("Ktor calls must be run in main thread")
    block()
  }
}

@OptIn(InternalCoroutinesApi::class)
internal fun CoroutineScope.childContext(): CoroutineContext {
  val ktorJob = Job()

  val ref = GuardedStableRef(ktorJob)

  val listenerDisposableHandle = coroutineContext[Job]!!.invokeOnCompletion(onCancelling = true) { cancelCause ->
    val parentJob = ref.state

    if (cancelCause is CancellationException)
      parentJob.cancel(cause = cancelCause)
    else
      parentJob.cancel()
  }

  ktorJob.invokeOnCompletion {
    listenerDisposableHandle.dispose()
  }

  return coroutineContext + ktorJob
}

actual val isMainThread: Boolean
  get() = NSThread.isMainThread
