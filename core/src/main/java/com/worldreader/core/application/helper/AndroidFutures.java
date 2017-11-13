package com.worldreader.core.application.helper;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class AndroidFutures {

  private AndroidFutures() {
    throw new AssertionError("No instances!");
  }

  public static <V> void addCallbackMainThread(ListenableFuture<V> future, FutureCallback<? super V> callback) {
    Futures.addCallback(future, callback, AndroidExecutors.uiThread());
  }

}
