package com.harmony.kotlin.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

@Suppress("UnnecessaryAbstractClass")

actual abstract class BaseTest {

  actual fun <T> runTest(block: suspend CoroutineScope.() -> T) {
    runBlocking { block() }
  }
}
