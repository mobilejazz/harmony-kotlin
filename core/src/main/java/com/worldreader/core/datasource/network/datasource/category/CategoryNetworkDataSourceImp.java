package com.worldreader.core.datasource.network.datasource.category;

import android.support.annotation.NonNull;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.datasource.model.CategoryEntity;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import retrofit2.Call;

import javax.inject.Inject;
import java.util.*;

public class CategoryNetworkDataSourceImp implements CategoryNetworkDataSource {

  public static final String TAG = CategoryNetworkDataSource.class.getSimpleName();

  public static final String ENDPOINT = "/categories";

  private final CategoryApiService categoryApiService;
  private final CountryCodeProvider countryCodeProvider;
  private final ErrorAdapter<Throwable> errorAdapter;
  private final Logger logger;

  @Inject public CategoryNetworkDataSourceImp(CategoryApiService categoryApiService, CountryCodeProvider countryCodeProvider,
      ErrorAdapter<Throwable> errorAdapter, Logger logger) {
    this.categoryApiService = categoryApiService;
    this.countryCodeProvider = countryCodeProvider;
    this.errorAdapter = errorAdapter;
    this.logger = logger;
  }

  @Override public void fetchCategories(final CompletionCallback<List<CategoryEntity>> callback) {
    final String iso3Language = countryCodeProvider.getLanguageIso3Code();
    categoryApiService.categories(iso3Language).enqueue(new retrofit2.Callback<List<CategoryEntity>>() {
      @Override
      public void onResponse(@NonNull final Call<List<CategoryEntity>> call, @NonNull final retrofit2.Response<List<CategoryEntity>> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            final List<CategoryEntity> categoryEntities = response.body();
            callback.onSuccess(categoryEntities);
          }
        } else {
          if (callback != null) {
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error));
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<List<CategoryEntity>> call, @NonNull final Throwable t) {
        if (callback != null) {
          callback.onError(errorAdapter.of(t));
        }
      }
    });
  }
}
