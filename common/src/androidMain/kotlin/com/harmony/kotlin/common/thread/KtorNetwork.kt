package com.harmony.kotlin.common.thread

internal actual suspend fun <R> network(block: suspend () -> R): R = block()
