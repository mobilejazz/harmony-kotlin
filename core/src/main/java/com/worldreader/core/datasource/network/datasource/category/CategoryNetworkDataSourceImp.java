package com.worldreader.core.datasource.network.datasource.category;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.deprecated.error.adapter.ErrorRetrofitAdapter;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.datasource.model.CategoryEntity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.*;

public class CategoryNetworkDataSourceImp implements CategoryNetworkDataSource {

  public static final String ENDPOINT = "/categories";

  private CategoryApiService categoryApiService;
  private CountryCodeProvider countryCodeProvider;

  private Logger logger;
  private ErrorAdapter<RetrofitError> errorAdapter = new ErrorRetrofitAdapter();
  public static final String TAG = CategoryNetworkDataSource.class.getSimpleName();

  @Inject public CategoryNetworkDataSourceImp(CategoryApiService categoryApiService,
      CountryCodeProvider countryCodeProvider, Logger logger) {
    this.categoryApiService = categoryApiService;
    this.countryCodeProvider = countryCodeProvider;
    this.logger = logger;
  }

  @Override public void fetchCategories(final CompletionCallback<List<CategoryEntity>> callback) {
    String iso3Language = countryCodeProvider.getLanguageIso3Code();
    categoryApiService.categories(iso3Language, new Callback<List<CategoryEntity>>() {
      @Override public void success(List<CategoryEntity> categoryEntities, Response response) {
        if (callback != null) {
          callback.onSuccess(categoryEntities);
        }
      }

      @Override public void failure(RetrofitError error) {
        if (callback != null) {
          logger.e(TAG, error.toString());
          callback.onError(errorAdapter.of(error));
        }
      }
    });
  }
}
