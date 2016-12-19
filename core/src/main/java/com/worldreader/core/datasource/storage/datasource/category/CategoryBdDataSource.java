package com.worldreader.core.datasource.storage.datasource.category;

import com.worldreader.core.datasource.model.CategoryEntity;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

import java.util.*;

public interface CategoryBdDataSource {

  List<CategoryEntity> obtains(String key) throws InvalidCacheException;

  void persist(String key, List<CategoryEntity> categories);
}
