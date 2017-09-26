package com.worldreader.core.application.helper;

import com.google.common.util.concurrent.FutureCallback;

import javax.annotation.Nullable;

public abstract class CancelableFutureCallback<V> implements FutureCallback<V> {

  private boolean canceled;

  public CancelableFutureCallback() {
    this.canceled = false;
  }

  @Override public final void onSuccess(@Nullable V result) {
    if (!canceled) {
      onResult(result);
    }
  }

  @Override public final void onFailure(Throwable t) {
    if (!canceled) {
      onError(t);
    }
  }

  void cancel() {
    this.canceled = true;
  }

  abstract public void onResult(@Nullable V result);

  abstract public void onError(Throwable t);

}
