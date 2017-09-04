package com.worldreader.core.concurrency;

import android.support.annotation.Nullable;

import java.util.concurrent.*;

public abstract class SafeCallable<T> implements Callable<T> {

  @Override public @Nullable T call() throws Exception {
    try {
      return safeCall();
    } catch (Throwable t) {
      onExceptionThrown(t);
      return null;
    }
  }

  protected abstract T safeCall() throws Throwable;

  protected abstract void onExceptionThrown(final Throwable t);
}
