package com.worldreader.core.domain.deprecated;

interface Callback<T, E> {

  void onSuccess(T result);

  void onError(E error);
}
