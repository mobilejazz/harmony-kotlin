package com.worldreader.core.domain.deprecated;

@Deprecated interface Callback<T, E> {

  void onSuccess(T result);

  void onError(E error);
}
