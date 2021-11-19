package com.mobilejazz.kmmsample.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ios.Ios

actual val engine: HttpClientEngine
  get() = Ios.create { }
