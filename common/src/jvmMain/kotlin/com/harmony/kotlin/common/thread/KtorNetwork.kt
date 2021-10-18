package com.harmony.kotlin.common.thread

actual suspend fun <R> network(block: suspend () -> R): R = block()

actual val isMainThread: Boolean
  get() = TODO("not implemented")
