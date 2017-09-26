package com.worldreader.core.domain.repository;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.domain.model.Category;

import java.util.*;

public interface CategoryRepository {

  void categories(CompletionCallback<List<Category>> callback);

  void categories(String language, CompletionCallback<List<Category>> callback);
}
