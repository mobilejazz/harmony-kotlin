package com.worldreader.core.datasource.network.datasource.category;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.datasource.model.CategoryEntity;

import java.util.*;

public interface CategoryNetworkDataSource {

  void fetchCategories(String language, CompletionCallback<List<CategoryEntity>> callback);

  void fetchCategories(CompletionCallback<List<CategoryEntity>> callback);
}
