package com.harmony.kotlin.common.thread

expect suspend fun <R> network(block: suspend () -> R): R
