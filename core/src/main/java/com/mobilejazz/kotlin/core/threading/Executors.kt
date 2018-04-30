package com.mobilejazz.kotlin.core.threading

import android.os.Handler
import android.os.Looper
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object AppExecutor : ListeningExecutorService by MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor())

object AppUiExecutor : Executor {

  private val handler: Handler = Handler(Looper.getMainLooper())

  override fun execute(runnable: Runnable?) {
    handler.post(runnable)
  }
}

object DirectExecutor : ListeningExecutorService by MoreExecutors.listeningDecorator(MoreExecutors.newDirectExecutorService())