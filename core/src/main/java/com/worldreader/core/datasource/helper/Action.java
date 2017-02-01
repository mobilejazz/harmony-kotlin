package com.worldreader.core.datasource.helper;

public interface Action<T> {

  boolean perform(T value);
}
