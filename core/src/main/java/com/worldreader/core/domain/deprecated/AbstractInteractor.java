package com.worldreader.core.domain.deprecated;

import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;

public abstract class AbstractInteractor<T, E> implements Interactor {

  protected InteractorExecutor executor;
  protected MainThread mainThread;

  public AbstractInteractor(InteractorExecutor executor, MainThread mainThread) {
    this.executor = executor;
    this.mainThread = mainThread;
  }

  public void performSuccessCallback(final DomainCallback<T, E> callback, final T result) {
    checkIfParameterIsNull(callback);
    checkIfResultIsNull(result);

    mainThread.post(new Runnable() {
      @Override public void run() {
        callback.onSuccess(result);
      }
    });
  }

  public void performErrorCallback(final DomainCallback<T, E> callback, final E error) {
    checkIfParameterIsNull(callback);
    checkIfErrorIsNull(error);

    mainThread.post(new Runnable() {
      @Override public void run() {
        callback.onError(error);
      }
    });
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private void checkIfParameterIsNull(DomainCallback<T, E> callback) {
    if (mainThread == null) {
      throw new IllegalArgumentException("mainThread == null");
    }

    if (callback == null) {
      throw new IllegalArgumentException("callback == null");
    }
  }

  private void checkIfResultIsNull(T result) {
    if (result == null) {
      throw new IllegalArgumentException("result == null");
    }
  }

  private void checkIfErrorIsNull(E error) {
    if (error == null) {
      throw new IllegalArgumentException("error == null");
    }
  }
}
