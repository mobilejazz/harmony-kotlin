package com.mobilejazz.harmony.kotlin.core.threading

import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.Executors

typealias Executor = ListeningExecutorService

object AppExecutor : Executor by MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor())

object DirectExecutor : Executor by MoreExecutors.listeningDecorator(MoreExecutors.newDirectExecutorService())

object MultiThreadExecutor : Executor by MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5))
