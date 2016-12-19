package com.worldreader.core.datasource.mapper;

import com.google.common.base.Preconditions;
import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.CategoryEntity;
import com.worldreader.core.domain.model.Category;

import java.util.*;

public class CategoryEntityDataMapper implements Mapper<Category, CategoryEntity> {

  @Override public Category transform(CategoryEntity data) {
    Preconditions.checkNotNull(data, "CategoryEntity must be not null");

    Category category = new Category();
    category.setId(data.getId());
    category.setTitle(data.getTitle());
    category.setColor(data.getColor());
    category.setIcon(data.getIcon());
    category.setSubCategories(transform(data.getSubCategoryEntities()));

    return category;
  }

  @Override public List<Category> transform(List<CategoryEntity> data) {
    Preconditions.checkNotNull(data, "CategoryEntities must be not null");

    List<Category> categories = new ArrayList<>();
    for (CategoryEntity categoryEntity : data) {
      categories.add(transform(categoryEntity));
    }

    return categories;
  }

  @Override public CategoryEntity transformInverse(Category data) {
    throw new IllegalStateException("transformInverse(Category data) is not supported");
  }

  @Override public List<CategoryEntity> transformInverse(List<Category> data) {
    throw new IllegalStateException("transformInverse(List<Category> data) is not supported");
  }
}
