package com.worldreader.core.domain.deprecated.executor;

import com.mobilejazz.logger.library.Logger;

import java.util.concurrent.*;

@Deprecated
public class InteractorThreadFactory implements ThreadFactory {

  public static final String TAG = InteractorThreadFactory.class.getSimpleName();

  public static final String THREAD_TAG = "InteractorThread-";
  public static final int THREAD_PRIORITY = 4; // We don't want to compete with MainUi thread

  private final Logger logger;

  public InteractorThreadFactory(Logger logger) {
    this.logger = logger;
  }

  @Override public Thread newThread(Runnable r) {
    Thread t = new Thread(r);
    t.setName(THREAD_TAG + System.currentTimeMillis());
    t.setPriority(THREAD_PRIORITY);
    t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override public void uncaughtException(Thread thread, Throwable ex) {
        logger.e(TAG, "Thread = " + thread.getName() + ", error = " + ex.getMessage());
      }
    });
    return t;
  }
}
