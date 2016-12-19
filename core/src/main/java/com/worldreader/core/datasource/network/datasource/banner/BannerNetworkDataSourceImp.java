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

  private BannerApiService bannerApiService;
  private Logger logger;
  private CountryCodeProvider countryCodeProvider;

  private ErrorAdapter<RetrofitError> errorAdapter = new ErrorRetrofitAdapter();
  public static final String TAG = BannerNetworkDataSource.class.getSimpleName();

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
}
