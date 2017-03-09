package com.worldreader.core.datasource.network.model;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import com.worldreader.core.common.annotation.Immutable;

import java.util.*;

@Immutable public class UpdateUserFavoriteCategoriesNetworkBody {

  @SerializedName("favoriteCategories") private final String favoriteCategories;

  public UpdateUserFavoriteCategoriesNetworkBody(List<Integer> categories) {
    this.favoriteCategories = new ToCommaSeparatedStringFunction().apply(categories);
  }

  public String getFavoriteCategories() {
    return favoriteCategories;
  }

  private static class ToCommaSeparatedStringFunction implements Function<List<Integer>, String> {

    @Override public String apply(List<Integer> input) {
      Preconditions.checkNotNull(input, "input == null");

      String result = "";
      for (Integer categoryId : input) {
        result += String.valueOf(categoryId) + ",";
      }

      return result;
    }

  }
}
