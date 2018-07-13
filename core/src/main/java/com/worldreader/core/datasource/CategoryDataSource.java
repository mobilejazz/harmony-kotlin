package com.worldreader.core.datasource;

import android.support.annotation.Nullable;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.datasource.helper.url.URLProvider;
import com.worldreader.core.datasource.mapper.CategoryEntityDataMapper;
import com.worldreader.core.datasource.model.CategoryEntity;
import com.worldreader.core.datasource.network.datasource.category.CategoryNetworkDataSource;
import com.worldreader.core.datasource.network.datasource.category.CategoryNetworkDataSourceImp;
import com.worldreader.core.datasource.storage.datasource.category.CategoryBdDataSource;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;
import com.worldreader.core.domain.model.Category;
import com.worldreader.core.domain.repository.CategoryRepository;

import javax.inject.Inject;
import java.util.*;

public class CategoryDataSource implements CategoryRepository {

  private CategoryBdDataSource bdDataSource;
  private CategoryNetworkDataSource networkDataSource;
  private CategoryEntityDataMapper mapper;
  private CountryCodeProvider countryCodeProvider;

  @Inject public CategoryDataSource(CategoryBdDataSource bdDataSource, CategoryNetworkDataSource networkDataSource, CategoryEntityDataMapper mapper,
      CountryCodeProvider countryCodeProvider) {
    this.bdDataSource = bdDataSource;
    this.networkDataSource = networkDataSource;
    this.mapper = mapper;
    this.countryCodeProvider = countryCodeProvider;
  }

  @Override public void categories(final CompletionCallback<List<Category>> callback) {
    categories(countryCodeProvider.getLanguageIso3Code(), callback);
  }

  @Override public void categories(@Nullable String language, final CompletionCallback<List<Category>> callback) {
    if (language == null) {
      language = countryCodeProvider.getLanguageIso3Code();
    }
    final String key = URLProvider.withEndpoint(CategoryNetworkDataSourceImp.ENDPOINT).addLanguages(language).build();

    try {
      List<CategoryEntity> categoryEntities = bdDataSource.obtains(key);
      List<Category> categories = mapper.transform(categoryEntities);

      responseCategoriesLoaded(categories, callback);
    } catch (InvalidCacheException e) {
      networkDataSource.fetchCategories(new CompletionCallback<List<CategoryEntity>>() {
        @Override public void onSuccess(List<CategoryEntity> categoryEntities) {
          bdDataSource.persist(key, categoryEntities);

          List<Category> categories = mapper.transform(categoryEntities);
          responseCategoriesLoaded(categories, callback);
        }

        @Override public void onError(ErrorCore errorCore) {
          if (callback != null) {
            callback.onError(errorCore);
          }
        }
      });
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private void responseCategoriesLoaded(List<Category> categories, CompletionCallback<List<Category>> callback) {
    if (callback != null) {
      callback.onSuccess(categories);
    }
  }
}
