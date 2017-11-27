package com.worldreader.core.common.deprecated.callback;

import com.worldreader.core.common.deprecated.error.ErrorCore;

@Deprecated
public interface CompletionCallback<T> {

  void onSuccess(T result);

  void onError(ErrorCore error);
}
