package com.harmony.kotlin.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest

@ExperimentalCoroutinesApi
internal actual fun <T> runTest(block: suspend () -> T) = runBlockingTest { block() }
