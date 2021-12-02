package com.mobilejazz.kmmsample.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual val engine: HttpClientEngine
  get() = OkHttp.create {
    threadsCount = 1
  }
