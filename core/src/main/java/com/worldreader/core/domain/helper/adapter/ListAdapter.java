package com.worldreader.core.domain.helper.adapter;

import java.util.*;

public interface ListAdapter<T, V> extends Adapter<T, V> {

  List<T> transform(List<V> elements);

}
