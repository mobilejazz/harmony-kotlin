package com.mobilejazz.harmony.kotlin.core.threading

import android.os.Handler
import android.os.Looper
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

typealias Executor = ListeningExecutorService

object AppExecutor : Executor by MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor())

object DirectExecutor : Executor by MoreExecutors.listeningDecorator(MoreExecutors.newDirectExecutorService())

object MultiThreadExecutor: Executor by MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5))

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

    override fun execute(runnable: Runnable?) {
        handler.post(runnable)
    }
}
