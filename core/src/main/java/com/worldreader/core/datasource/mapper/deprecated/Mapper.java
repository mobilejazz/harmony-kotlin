package com.worldreader.core.datasource.mapper.deprecated;

import java.util.*;

@Deprecated public interface Mapper<T, D> {

  T transform(D data);

  List<T> transform(List<D> data);

  D transformInverse(T data);

  List<D> transformInverse(List<T> data);
}
