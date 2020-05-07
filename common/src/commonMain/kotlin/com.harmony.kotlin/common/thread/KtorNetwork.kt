package com.harmony.kotlin.common.thread

internal expect suspend fun <R> network(block: suspend () -> R): R
