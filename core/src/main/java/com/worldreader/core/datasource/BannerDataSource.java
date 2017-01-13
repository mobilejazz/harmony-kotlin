package com.worldreader.core.datasource;

import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.datasource.helper.url.URLProvider;
import com.worldreader.core.datasource.mapper.BannerEntityDataMapper;
import com.worldreader.core.datasource.model.BannerEntity;
import com.worldreader.core.datasource.network.datasource.banner.BannerNetworkDataSource;
import com.worldreader.core.datasource.network.datasource.banner.BannerNetworkDataSourceImp;
import com.worldreader.core.datasource.storage.datasource.banner.BannerBdDataSource;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;
import com.worldreader.core.domain.model.Banner;
import com.worldreader.core.domain.repository.BannerRepository;

import javax.inject.Inject;
import java.util.*;

public class BannerDataSource implements BannerRepository {

  private BannerNetworkDataSource networkDataSource;
  private BannerBdDataSource bdDataSource;
  private BannerEntityDataMapper entityDataMapper;
  private CountryCodeProvider countryCodeProvider;

  @Inject public BannerDataSource(BannerNetworkDataSource networkDataSource,
      BannerBdDataSource bdDataSource, BannerEntityDataMapper entityDataMapper,
      CountryCodeProvider countryCodeProvider) {
    this.networkDataSource = networkDataSource;
    this.bdDataSource = bdDataSource;
    this.entityDataMapper = entityDataMapper;
    this.countryCodeProvider = countryCodeProvider;
  }

  @Override
  public void mainBanner(int index, int limit, final CompletionCallback<List<Banner>> callback) {
    final String key = URLProvider.withEndpoint(BannerNetworkDataSourceImp.MAIN_BANNER_ENDPOINT)
        .addIndex(index)
        .addLimit(limit)
        .addCountryCode(countryCodeProvider.getCountryCode())
        .build();

    try {
      List<BannerEntity> bannerEntities = bdDataSource.obtains(key);
      List<Banner> banners = transform(bannerEntities);

      responseBannersLoaded(callback, banners);
    } catch (InvalidCacheException invalid) {
      networkDataSource.fetchMainBanners(index, limit,
          new CompletionCallback<List<BannerEntity>>() {
            @Override public void onSuccess(List<BannerEntity> result) {
              bdDataSource.persist(key, result);

              List<Banner> banners = transform(result);
              responseBannersLoaded(callback, banners);
            }

            @Override public void onError(ErrorCore error) {
              if (callback != null) {
                callback.onError(error);
              }
            }
          });
    }
  }

  @Override public void collectionBanner(int index, int limit,
      final CompletionCallback<List<Banner>> callback) {
    final String key =
        URLProvider.withEndpoint(BannerNetworkDataSourceImp.COLLECTION_BANNER_ENDPOINT)
            .addIndex(index)
            .addLimit(limit)
            .addCountryCode(countryCodeProvider.getCountryCode())
            .build();

    try {
      List<BannerEntity> bannerEntities = bdDataSource.obtains(key);
      List<Banner> banners = transform(bannerEntities);

      responseBannersLoaded(callback, banners);
    } catch (InvalidCacheException invalid) {
      networkDataSource.fetchCollectionBanners(index, limit,
          new CompletionCallback<List<BannerEntity>>() {
            @Override public void onSuccess(List<BannerEntity> result) {
              bdDataSource.persist(key, result);

              List<Banner> banners = transform(result);
              responseBannersLoaded(callback, banners);
            }

            @Override public void onError(ErrorCore error) {
              if (callback != null) {
                callback.onError(error);
              }
            }
          });
    }
  }

  @Override public void getAll(String identifier, int index, int limit,
      final Callback<List<Banner>> callback) {
    final String key = URLProvider.withEndpoint(identifier)
        .addIndex(index)
        .addLimit(limit)
        .addCountryCode(countryCodeProvider.getCountryCode())
        .build();

    try {
      List<BannerEntity> bannerEntitiesFromCache = bdDataSource.obtains(key);
      List<Banner> banners = transform(bannerEntitiesFromCache);

      if (callback != null) {
        callback.onSuccess(banners);
      }
    } catch (InvalidCacheException e) {
      networkDataSource.getAll(identifier, index, limit, new Callback<List<BannerEntity>>() {
        @Override public void onSuccess(final List<BannerEntity> bannerEntities) {
          bdDataSource.persist(key, bannerEntities);

          List<Banner> banners = transform(bannerEntities);

          if (callback != null) {
            callback.onSuccess(banners);
          }
        }

        @Override public void onError(Throwable e) {
          if (callback != null) {
            callback.onError(e);
          }
        }
      });
    }

  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private List<Banner> transform(List<BannerEntity> bannerEntities) {
    return entityDataMapper.transform(bannerEntities);
  }

  private void responseBannersLoaded(CompletionCallback<List<Banner>> callback,
      List<Banner> banners) {
    if (callback != null) {
      callback.onSuccess(banners);
    }
  }
}
