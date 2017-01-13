package com.worldreader.core.datasource.network.datasource.banner;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.deprecated.error.adapter.ErrorRetrofitAdapter;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.datasource.model.BannerEntity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.*;

public class BannerNetworkDataSourceImp implements BannerNetworkDataSource {

  public static final String MAIN_BANNER_ENDPOINT = "/banners/main";
  public static final String COLLECTION_BANNER_ENDPOINT = "/banners/collections";
  public static final String READ_TO_KIDS_ENDPOINT = "/banners/r2kmain";
  public static final String TAG = BannerNetworkDataSource.class.getSimpleName();
  private BannerApiService bannerApiService;
  private Logger logger;
  private CountryCodeProvider countryCodeProvider;
  private ErrorAdapter<RetrofitError> errorAdapter = new ErrorRetrofitAdapter();

  @Inject public BannerNetworkDataSourceImp(CountryCodeProvider countryCodeProvider,
      BannerApiService bannerApiService, Logger logger) {
    this.countryCodeProvider = countryCodeProvider;
    this.bannerApiService = bannerApiService;
    this.logger = logger;
  }

  @Override public void fetchMainBanners(int index, int limit,
      final CompletionCallback<List<BannerEntity>> callback) {

    bannerApiService.mainBanners(index, limit, countryCodeProvider.getCountryCode(),
        new Callback<List<BannerEntity>>() {
          @Override public void success(List<BannerEntity> bannerEntities, Response response) {
            if (callback != null) {
              callback.onSuccess(bannerEntities);
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

  @Override public void fetchCollectionBanners(int index, int limit,
      final CompletionCallback<List<BannerEntity>> callback) {

    bannerApiService.collectionBanners(index, limit, countryCodeProvider.getCountryCode(),
        new Callback<List<BannerEntity>>() {
          @Override public void success(List<BannerEntity> bannerEntities, Response response) {
            if (callback != null) {
              callback.onSuccess(bannerEntities);
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

  @Override public void getAll(String type, int index, int limit,
      final com.worldreader.core.common.callback.Callback<List<BannerEntity>> callback) {
    bannerApiService.banners(type, index, limit, countryCodeProvider.getCountryCode(),
        new Callback<List<BannerEntity>>() {
          @Override public void success(List<BannerEntity> bannerEntities, Response response) {
            if (callback != null) {
              callback.onSuccess(bannerEntities);
            }
          }

          @Override public void failure(RetrofitError error) {
            if (callback != null) {
              logger.e(TAG, error.toString());
              callback.onError(errorAdapter.of(error).getCause());
            }
          }
        });
  }

  @Override public void get(int id, String type,
      final com.worldreader.core.common.callback.Callback<BannerEntity> callback) {
    bannerApiService.banner(id, type, countryCodeProvider.getCountryCode(),
        new Callback<BannerEntity>() {
          @Override public void success(BannerEntity bannerEntity, Response response) {
            if (callback != null) {
              callback.onSuccess(bannerEntity);
            }
          }

          @Override public void failure(RetrofitError error) {
            if (callback != null) {
              logger.e(TAG, error.toString());
              callback.onError(errorAdapter.of(error).getCause());
            }
          }
        });
  }
}
