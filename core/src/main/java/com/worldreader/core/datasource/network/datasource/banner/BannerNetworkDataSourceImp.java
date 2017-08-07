package com.worldreader.core.datasource.network.datasource.banner;

import android.support.annotation.NonNull;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.datasource.model.BannerEntity;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import retrofit2.Call;

import javax.inject.Inject;
import java.util.*;

public class BannerNetworkDataSourceImp implements BannerNetworkDataSource {

  public static final String TAG = BannerNetworkDataSource.class.getSimpleName();

  public static final String MAIN_BANNER_ENDPOINT = "/banners/main";
  public static final String COLLECTION_BANNER_ENDPOINT = "/banners/collections";

  private final BannerApiService bannerApiService;
  private final CountryCodeProvider countryCodeProvider;
  private final ErrorAdapter<Throwable> errorAdapter;
  private final Logger logger;

  @Inject
  public BannerNetworkDataSourceImp(CountryCodeProvider countryCodeProvider, BannerApiService bannerApiService, ErrorAdapter<Throwable> errorAdapter,
      Logger logger) {
    this.countryCodeProvider = countryCodeProvider;
    this.bannerApiService = bannerApiService;
    this.errorAdapter = errorAdapter;
    this.logger = logger;
  }

  @Override public void fetchMainBanners(int index, int limit, final CompletionCallback<List<BannerEntity>> callback) {
    bannerApiService.mainBanners(index, limit, countryCodeProvider.getCountryCode()).enqueue(new retrofit2.Callback<List<BannerEntity>>() {
      @Override public void onResponse(@NonNull final Call<List<BannerEntity>> call, @NonNull final retrofit2.Response<List<BannerEntity>> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            final List<BannerEntity> bannerEntities = response.body();
            callback.onSuccess(bannerEntities);
          }
        } else {
          if (callback != null) {
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error));
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<List<BannerEntity>> call, final Throwable t) {
        if (callback != null) {
          callback.onError(errorAdapter.of(t));
        }
      }
    });
  }

  @Override public void fetchCollectionBanners(int index, int limit, final CompletionCallback<List<BannerEntity>> callback) {
    bannerApiService.collectionBanners(index, limit, countryCodeProvider.getCountryCode()).enqueue(new retrofit2.Callback<List<BannerEntity>>() {
      @Override public void onResponse(@NonNull final Call<List<BannerEntity>> call, @NonNull final retrofit2.Response<List<BannerEntity>> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            final List<BannerEntity> bannerEntities = response.body();
            callback.onSuccess(bannerEntities);
          }
        } else {
          if (callback != null) {
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error));
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<List<BannerEntity>> call, @NonNull final Throwable t) {
        if (callback != null) {
          callback.onError(errorAdapter.of(t));
        }
      }
    });
  }

  @Override public void getAll(String type, int index, int limit, final com.worldreader.core.common.callback.Callback<List<BannerEntity>> callback) {
    bannerApiService.banners(type, index, limit, countryCodeProvider.getCountryCode()).enqueue(new retrofit2.Callback<List<BannerEntity>>() {
      @Override public void onResponse(@NonNull final Call<List<BannerEntity>> call, @NonNull final retrofit2.Response<List<BannerEntity>> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            final List<BannerEntity> bannerEntities = response.body();
            callback.onSuccess(bannerEntities);
          }
        } else {
          if (callback != null) {
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error).getCause());
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<List<BannerEntity>> call, @NonNull final Throwable t) {
        if (callback != null) {
          callback.onError(errorAdapter.of(t).getCause());
        }
      }
    });
  }

  @Override public void get(int id, String type, final com.worldreader.core.common.callback.Callback<BannerEntity> callback) {
    bannerApiService.banner(id, type, countryCodeProvider.getCountryCode()).enqueue(new retrofit2.Callback<BannerEntity>() {
      @Override public void onResponse(@NonNull final Call<BannerEntity> call, @NonNull final retrofit2.Response<BannerEntity> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            final BannerEntity bannerEntity = response.body();
            callback.onSuccess(bannerEntity);
          }
        } else {
          if (callback != null) {
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error).getCause());
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<BannerEntity> call, @NonNull final Throwable t) {
        if (callback != null) {
          callback.onError(errorAdapter.of(t).getCause());
        }
      }
    });
  }
}
