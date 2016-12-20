package com.worldreader.core.domain.helper.adapter;

import com.worldreader.core.domain.model.Category;

import java.util.*;

public class CategoryToIntAdapter implements ListAdapter<Integer, Category> {

  @Override public List<Integer> transform(List<Category> elements) {
    List<Integer> transformed = new ArrayList<>(elements.size());
    for (Category category : elements) {
      transformed.add(transform(category));
    }
    return transformed;
  }

  public List<Integer> transformWithSubCats(List<Category> elements) {
    if (elements != null) {
      List<Integer> transformed = new ArrayList<>(elements.size());
      for (Category category : elements) {
        transformed.add(transform(category));
        for (Category subCat : category.getSubCategories()) {
          transformed.add(transform(subCat));
        }
      }
      return transformed;
    } else {
      return new ArrayList<>();
    }
  }

  @Override public Integer transform(Category element) {
    return element.getId();
  }
}
