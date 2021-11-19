package com.mobilejazz.kmmsample.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

actual fun dispatcher(): CoroutineDispatcher =
  Executors.newSingleThreadExecutor().asCoroutineDispatcher()
