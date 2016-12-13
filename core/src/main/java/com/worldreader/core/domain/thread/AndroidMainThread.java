package com.worldreader.core.domain.thread;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.*;

public class AndroidMainThread implements MainThread {

  private final Executor executor;

  public AndroidMainThread() {
    executor = new MainThreadExecutor(new Handler(Looper.getMainLooper()));
  }

  @Override public void post(Runnable runnable) {
    executor.execute(runnable);
  }

  @Override public boolean isMainThread() {
    return Thread.currentThread() == Looper.getMainLooper().getThread();
  }

  @Override public Executor getMainThreadExecutor() {
    return executor;
  }

  private static class MainThreadExecutor implements Executor {

    private final Handler handler;

    MainThreadExecutor(Handler handler) {
      this.handler = handler;
    }

    @Override public void execute(@NonNull Runnable runnable) {
      handler.post(runnable);
    }
  }

}
