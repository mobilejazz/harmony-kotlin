package com.harmony.kotlin.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry

open class InstrumentationTest {
  val testContext: Context
    get() = InstrumentationRegistry.getInstrumentation().context
  val appContext: Context
    get() = InstrumentationRegistry.getInstrumentation().context
}
