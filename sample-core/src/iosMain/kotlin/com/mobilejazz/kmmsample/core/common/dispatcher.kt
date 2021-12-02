package com.mobilejazz.kmmsample.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun dispatcher(): CoroutineDispatcher = Dispatchers.Main
