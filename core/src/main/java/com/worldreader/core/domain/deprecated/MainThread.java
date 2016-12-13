package com.worldreader.core.domain.deprecated;

/**
 * Abstraction used to allow interactor callbacks to be executed in the main UI thread.
 */
public interface MainThread {
  void post(final Runnable runnable);

  boolean isMainThread();
}
