package com.harmony.kotlin.common

import kotlinx.coroutines.runBlocking

internal actual fun <T> runTest(block: suspend () -> T) {
  runBlocking { block }
}
