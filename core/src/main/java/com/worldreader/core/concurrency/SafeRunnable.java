package com.worldreader.core.concurrency;

public abstract class SafeRunnable implements Runnable {

  @Override public void run() {
    try {
      safeRun ();
    } catch (Throwable t) {
      onExceptionThrown (t);
    }
  }

  protected abstract void safeRun() throws Throwable;

  protected abstract void onExceptionThrown(final Throwable t);
}
