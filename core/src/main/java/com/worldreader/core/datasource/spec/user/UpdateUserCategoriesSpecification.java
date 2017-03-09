package com.worldreader.core.datasource.spec.user;

import com.google.common.base.Preconditions;
import com.worldreader.core.common.annotation.Immutable;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

import java.util.*;

@Immutable public class UpdateUserCategoriesSpecification extends RepositorySpecification {

  private final List<Integer> categories;

  public UpdateUserCategoriesSpecification(List<Integer> categories) {
    this.categories = Preconditions.checkNotNull(categories, "categories == null");
  }

  public List<Integer> getCategories() {
    return categories;
  }

  @Override public String getIdentifier() {
    return null;
  }

}
