package com.worldreader.core.domain.deprecated;

public abstract class DomainCallback<T, E> implements Callback<T, E> {

  private MainThread mainThread;

  public DomainCallback(MainThread mainThread) {
    this.mainThread = mainThread;
  }

  @Override public void onSuccess(T result) {
    boolean isMainThread = this.mainThread.isMainThread();

    if (isMainThread) {
      onSuccessResult(result);
    } else {
      throw new IllegalStateException("onSuccess(T result) should be work in the MainThread");
    }
  }

  @Override public void onError(E error) {
    boolean isMainThread = this.mainThread.isMainThread();

    if (isMainThread) {
      onErrorResult(error);
    } else {
      throw new IllegalStateException("onError(E error) should be work in the MainThread");
    }
  }

  public abstract void onSuccessResult(T result);

  public abstract void onErrorResult(E result);
}
