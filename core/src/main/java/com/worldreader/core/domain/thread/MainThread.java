package com.worldreader.core.domain.thread;

import java.util.concurrent.Executor;

@Deprecated
public interface MainThread {

  void post(final Runnable runnable);

  boolean isMainThread();

  Executor getMainThreadExecutor();
}
