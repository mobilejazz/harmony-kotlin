package com.worldreader.core.application.helper;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.*;

public interface InteractorHandler {

  <T> void addCallbackMainThread(ListenableFuture<T> listenableFuture, FutureCallback<T> callback);

  <T> void addCallback(ListenableFuture<T> listenableFuture, FutureCallback<T> callback);

  <T> void addCallback(ListenableFuture<T> listenableFuture, FutureCallback<T> callback, Executor executor);

}
