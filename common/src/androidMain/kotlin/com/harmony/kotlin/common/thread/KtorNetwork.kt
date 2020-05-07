package com.harmony.kotlin.common.thread

actual suspend fun <R> network(block: suspend () -> R): R = block()
