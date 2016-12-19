package com.worldreader.core.datasource.network.datasource.collection;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.deprecated.error.adapter.ErrorRetrofitAdapter;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.datasource.model.CollectionEntity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.*;

public class CollectionNetworkDataSourceImp implements CollectionNetworkDataSource {

  public static final String ENDPOINT = "/collections";

  private CountryCodeProvider countryCodeProvider;
  private CollectionApiService collectionApiService;
  private ErrorAdapter<RetrofitError> errorAdapter = new ErrorRetrofitAdapter();
  private Logger logger;
  public static final String TAG = CollectionNetworkDataSource.class.getSimpleName();

  @Inject public CollectionNetworkDataSourceImp(CollectionApiService collectionApiService,
      CountryCodeProvider countryCodeProvider, Logger logger) {
    this.countryCodeProvider = countryCodeProvider;
    this.collectionApiService = collectionApiService;
    this.logger = logger;
  }

  @Override
  public void fetchCollections(final CompletionCallback<List<CollectionEntity>> callback) {
    String countryIsoCode = this.countryCodeProvider.getCountryCode();

    collectionApiService.collections(countryIsoCode, new Callback<List<CollectionEntity>>() {
      @Override public void success(List<CollectionEntity> collectionEntities, Response response) {
        if (callback != null) {
          callback.onSuccess(collectionEntities);
        }
      }

      @Override public void failure(RetrofitError error) {
        if (callback != null) {
          logger.d(TAG, error.toString());
          callback.onError(errorAdapter.of(error));
        }
      }
    });
  }

  @Override public void fetchCollection(int collectionId,
      final CompletionCallback<CollectionEntity> callback) {
    String countryIsoCode = this.countryCodeProvider.getCountryCode();

    collectionApiService.collection(collectionId, countryIsoCode, new Callback<CollectionEntity>() {
      @Override public void success(CollectionEntity collectionEntity, Response response) {
        if (callback != null) {
          callback.onSuccess(collectionEntity);
        }
      }

      @Override public void failure(RetrofitError error) {
        if (callback != null) {
          logger.d(TAG, error.toString());
          callback.onError(errorAdapter.of(error));
        }
      }
    });
  }
}
