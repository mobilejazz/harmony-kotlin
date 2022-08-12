package com.mobilejazz.kmmsample.mvi.application

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

// Functions taken from this article: https://proandroiddev.com/how-to-collect-flows-lifecycle-aware-in-jetpack-compose-babd53582d0b

@Composable
private fun <T> rememberFlow(
  flow: Flow<T>,
  lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
): Flow<T> {
  return remember(key1 = flow, key2 = lifecycleOwner) { flow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED) }
}

@Composable
private fun <T : R, R> Flow<T>.collectAsStateLifecycleAware(
  initial: R,
  context: CoroutineContext = EmptyCoroutineContext
): State<R> {
  val lifecycleAwareFlow = rememberFlow(flow = this)
  return lifecycleAwareFlow.collectAsState(initial = initial, context = context)
}

@Suppress("StateFlowValueCalledInComposition")
@Composable
fun <T> StateFlow<T>.collectAsStateLifecycleAware(
  context: CoroutineContext = EmptyCoroutineContext
): State<T> = collectAsStateLifecycleAware(initial = value, context = context)
