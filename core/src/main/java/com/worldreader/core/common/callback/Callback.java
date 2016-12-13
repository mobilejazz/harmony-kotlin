package com.worldreader.core.common.callback;

public interface Callback<T> {

  void onSuccess(T t);

  void onError(Throwable e);

  interface Progress extends Callback {

    void onProgress(int progress);
  }
}


