package com.mobilejazz.harmony.kotlin.android.threading.extension

import com.mobilejazz.harmony.kotlin.android.threading.AppUiExecutor
import com.mobilejazz.harmony.kotlin.core.threading.extensions.Future
import com.mobilejazz.harmony.kotlin.core.threading.extensions.onComplete
import com.mobilejazz.harmony.kotlin.core.threading.extensions.onCompleteNullable


inline fun <A> Future<A>.onCompleteUi(
    crossinline onFailure: (Throwable) -> Unit,
    crossinline onSuccess: (A) -> Unit
): Future<A> =
    onComplete(executor = AppUiExecutor, onFailure = onFailure, onSuccess = onSuccess)

inline fun <A> Future<A>.onCompleteNullableUi(
    crossinline onFailure: (Throwable) -> Unit,
    crossinline onSuccess: (A?) -> Unit
): Future<A> =
    onCompleteNullable(executor = AppUiExecutor, onFailure = onFailure, onSuccess = onSuccess)