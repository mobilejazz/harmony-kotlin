package com.harmony.kotlin.common

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Suppress("UnnecessaryAbstractClass")
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
actual abstract class BaseTest {

  @OptIn(ExperimentalCoroutinesApi::class)
  actual fun <T> runTest(block: suspend CoroutineScope.() -> T) {
    kotlinx.coroutines.test.runTest { block() }
  }
}
