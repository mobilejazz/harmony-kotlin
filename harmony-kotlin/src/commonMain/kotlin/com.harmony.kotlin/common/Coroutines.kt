package com.harmony.kotlin.common

internal expect fun <T> runTest(block: suspend () -> T)
