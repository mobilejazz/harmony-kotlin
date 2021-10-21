package com.harmony.kotlin.common.thread

import platform.Foundation.NSThread

actual suspend fun <R> network(block: suspend () -> R): R = block()

actual val isMainThread: Boolean
  get() = NSThread.isMainThread
