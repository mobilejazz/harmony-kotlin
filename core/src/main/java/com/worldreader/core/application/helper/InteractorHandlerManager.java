package com.worldreader.core.application.helper;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.domain.thread.MainThread;

import java.util.concurrent.*;

public class InteractorHandlerManager implements InteractorHandler {

  private final MainThread mainThread;

  public InteractorHandlerManager(MainThread mainThread) {
    this.mainThread = mainThread;
  }

  @Override public <T> void addCallbackMainThread(ListenableFuture<T> listenableFuture, FutureCallback<T> callback) {
    Futures.addCallback(listenableFuture, callback, mainThread.getMainThreadExecutor());
  }

  @Override public <T> void addCallback(ListenableFuture<T> listenableFuture, FutureCallback<T> callback) {
    Futures.addCallback(listenableFuture, callback);
  }

  @Override public <T> void addCallback(ListenableFuture<T> listenableFuture, FutureCallback<T> callback, Executor executor) {
    Futures.addCallback(listenableFuture, callback, executor);
  }

}
