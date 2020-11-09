package com.harmony.kotlin.common.thread

import platform.Foundation.NSThread

actual suspend fun <R> network(block: suspend () -> R): R {
    // This is a patch to prevent a crash thrown by ktor https://github.com/ktorio/ktor/issues/1165
    // This issue is now solved but still not released. In the future should not be necessary
    return try {
      block()
    } catch (t:Throwable) {
      throw Exception(t)
    }
}

actual val isMainThread: Boolean
  get() = NSThread.isMainThread
