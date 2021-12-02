package com.harmony.kotlin.common

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
actual abstract class BaseTest {

  @OptIn(ExperimentalCoroutinesApi::class)
  actual fun <T> runTest(block: suspend CoroutineScope.() -> T) {
    runBlockingTest { block() }
  }
}
