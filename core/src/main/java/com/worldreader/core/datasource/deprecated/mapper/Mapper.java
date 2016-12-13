package com.worldreader.core.datasource.deprecated.mapper;

import android.support.annotation.NonNull;

import java.util.*;

public interface Mapper<T, K> {

  @NonNull T transform(@NonNull final K data);

  List<T> transform(@NonNull final List<K> data);

  @NonNull K transformInverse(@NonNull final T data);

  List<K> transformInverse(@NonNull final List<T> data);
}