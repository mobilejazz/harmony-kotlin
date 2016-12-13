package com.worldreader.core.common.deprecated.error.adapter;

import com.worldreader.core.common.deprecated.error.ErrorCore;

public interface ErrorAdapter<T> {

  ErrorCore of(T error);
}
