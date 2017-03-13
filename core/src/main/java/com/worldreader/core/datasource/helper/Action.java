package com.worldreader.core.datasource.helper;

public interface Action<T, R> {

  R perform(T value);
}
