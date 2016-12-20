package com.worldreader.core.domain.helper.adapter;

public interface Adapter<T, V> {

  T transform(V element);

}
