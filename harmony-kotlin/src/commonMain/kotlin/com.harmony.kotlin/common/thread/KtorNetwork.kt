package com.harmony.kotlin.common.thread

@Deprecated("Using this is not necessary anymore")
expect suspend fun <R> network(block: suspend () -> R): R

expect val isMainThread: Boolean
