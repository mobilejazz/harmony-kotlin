package com.worldreader.core.datasource.network.datasource.collection;

import android.support.annotation.NonNull;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.datasource.model.CollectionEntity;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;

public class CollectionNetworkDataSourceImp implements CollectionNetworkDataSource {

  public static final String TAG = CollectionNetworkDataSource.class.getSimpleName();

  public static final String ENDPOINT = "/collections";

  private final CountryCodeProvider countryCodeProvider;
  private final CollectionApiService collectionApiService;
  private final ErrorAdapter<Throwable> errorAdapter;
  private Logger logger;

  @Inject public CollectionNetworkDataSourceImp(CollectionApiService collectionApiService, ErrorAdapter<Throwable> errorAdapter,
      CountryCodeProvider countryCodeProvider, Logger logger) {
    this.errorAdapter = errorAdapter;
    this.countryCodeProvider = countryCodeProvider;
    this.collectionApiService = collectionApiService;
    this.logger = logger;
  }

  @Override public void fetchCollections(final CompletionCallback<List<CollectionEntity>> callback) {
    final String countryIsoCode = this.countryCodeProvider.getCountryCode();

    collectionApiService.collections(countryIsoCode).enqueue(new retrofit2.Callback<List<CollectionEntity>>() {
      @Override
      public void onResponse(@NonNull final Call<List<CollectionEntity>> call, @NonNull final retrofit2.Response<List<CollectionEntity>> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            final List<CollectionEntity> collectionEntities = response.body();
            callback.onSuccess(collectionEntities);
          }
        } else {
          if (callback != null) {
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error));
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<List<CollectionEntity>> call, @NonNull final Throwable t) {
        if (callback != null) {
          callback.onError(errorAdapter.of(t));
        }
      }
    });
  }

  @Override public void fetchCollection(int collectionId, final CompletionCallback<CollectionEntity> callback) {
    final String countryIsoCode = this.countryCodeProvider.getCountryCode();

    collectionApiService.collection(collectionId, countryIsoCode).enqueue(new retrofit2.Callback<CollectionEntity>() {
      @Override public void onResponse(@NonNull final Call<CollectionEntity> call, @NonNull final retrofit2.Response<CollectionEntity> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            final CollectionEntity collectionEntity = response.body();
            callback.onSuccess(collectionEntity);
          }
        } else {
          if (callback != null) {
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error));
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<CollectionEntity> call, @NonNull final Throwable t) {
        if (callback != null) {
          callback.onError(errorAdapter.of(t));
        }
      }
    });
  }
}
