package com.mobilejazz.harmony.kotlin.android

import android.content.Context
import android.support.test.InstrumentationRegistry

open class InstrumentationTest {
  val testContext: Context
    get() = InstrumentationRegistry.getContext()
  val appContext: Context
    get() = InstrumentationRegistry.getTargetContext()
}