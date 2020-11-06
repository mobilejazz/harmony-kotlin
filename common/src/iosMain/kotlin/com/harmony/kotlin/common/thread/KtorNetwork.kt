package com.harmony.kotlin.common.thread

import co.touchlab.stately.concurrency.GuardedStableRef
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import platform.Foundation.NSThread
import kotlin.coroutines.CoroutineContext

actual suspend fun <R> network(block: suspend () -> R): R = coroutineScope {
  withContext(childContext()) {
     if (!isMainThread) error("Ktor calls must be run in main thread")
    // This is a patch to prevent a crash thrown by ktor https://github.com/ktorio/ktor/issues/1165
    // This issue is now solved but still not released. In the future should not be necessary
     try {
       block()
    } catch (t: Throwable) {
      throw Exception(t)
    }
  }

//  try {
//    return block()
//  } catch (t:Throwable) {
//    throw Exception(t)
//  }
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
