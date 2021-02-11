package com.mobilejazz.harmony.kotlin.android.threading

import android.os.Handler
import android.os.Looper
import com.google.common.util.concurrent.ListenableFuture
import com.mobilejazz.harmony.kotlin.core.threading.Executor
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


object AppUiExecutor : Executor {

  private val handler: Handler = Handler(Looper.getMainLooper())

  override fun shutdown() = TODO("not implemented")

  override fun <T : Any?> submit(task: Callable<T>?): ListenableFuture<T> = TODO("not implemented")

  override fun submit(task: Runnable?): ListenableFuture<*> = TODO("not implemented")

  override fun <T : Any?> submit(task: Runnable?, result: T): ListenableFuture<T> = TODO("not implemented")

  override fun shutdownNow(): MutableList<Runnable> = TODO("not implemented")

  override fun isShutdown(): Boolean = TODO("not implemented")

  override fun awaitTermination(p0: Long, p1: TimeUnit?): Boolean = TODO("not implemented")

  override fun <T : Any?> invokeAny(p0: MutableCollection<out Callable<T>>?): T = TODO("not implemented")

  override fun <T : Any?> invokeAny(p0: MutableCollection<out Callable<T>>?, p1: Long, p2: TimeUnit?): T = TODO("not implemented")

  override fun isTerminated(): Boolean = TODO("not implemented")

  override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>?): MutableList<Future<T>> = TODO("not implemented")

  override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>?, timeout: Long, unit: TimeUnit?): MutableList<Future<T>> = TODO("not implemented")

  override fun execute(runnable: Runnable) {
    handler.post(runnable)
  }
}
