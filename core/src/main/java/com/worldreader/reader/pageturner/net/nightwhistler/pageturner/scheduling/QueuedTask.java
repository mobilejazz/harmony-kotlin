/*
 * Copyright (C) 2013 Alex Kuiper
 *
 * This file is part of PageTurner
 *
 * PageTurner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PageTurner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PageTurner.  If not, see <http://www.gnu.org/licenses/>.*
 */

package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling;

import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.*;

/**
 * Wraps a QueueableAsyncTask and its parameters, so that it can be executed later.
 * <p>
 * It's essentially a simple Command Object for tasks.
 */
public class QueuedTask<A, B, C> {

  private static final ThreadFactory READER_THREAD_FACTORY = new ThreadFactory() {
    @Override public Thread newThread(@NonNull Runnable r) {
      final Thread t = new Thread(r);
      t.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
      t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
        @Override public void uncaughtException(Thread t, Throwable e) {
          Log.e("ReaderThread-" + t.getId(), "Uncaught exception: ", e);
        }
      });
      return t;
    }
  };

  public static final ListeningExecutorService READER_THREAD_EXECUTOR =
      MoreExecutors.listeningDecorator(
          new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), READER_THREAD_FACTORY) {
            @Override protected void beforeExecute(Thread t, Runnable r) {
              super.beforeExecute(t, r);
              Log.e("READER_THREAD_EXECUTOR", "Thread: " + t.getId() + " | Runnable: " + r.toString());
            }

            @Override public boolean allowsCoreThreadTimeOut() {
              return true;
            }
          }
      );

  private QueueableAsyncTask<A, B, C> task;
  private A[] parameters;

  private boolean executing = false;

  public QueuedTask(QueueableAsyncTask<A, B, C> task, A[] params) {
    this.task = task;
    this.parameters = params;
  }

  public void execute() {
    if (executing) {
      throw new IllegalStateException("Already executed, cannot execute twice.");
    }

    executing = true;

    task.executeOnExecutor(READER_THREAD_EXECUTOR, parameters);
  }

  public boolean isExecuting() {
    return executing;
  }

  public void cancel() {
    this.task.requestCancellation();
  }

  public QueueableAsyncTask<A, B, C> getTask() {
    return task;
  }

  @Override public String toString() {
    return task.toString();
  }
}



