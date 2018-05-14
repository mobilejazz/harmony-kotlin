package com.mobilejazz.kotlin.core.threading

import com.google.common.util.concurrent.ListenableFuture
import com.mobilejazz.kotlin.core.threading.extensions.emptyListenableFuture
import com.mobilejazz.kotlin.core.threading.extensions.toListenableFuture

typealias Future<K> = ListenableFuture<K>

fun <T> T.toFuture(): Future<T> = this.toListenableFuture()

fun emptyFuture(): Future<Void> = emptyListenableFuture()