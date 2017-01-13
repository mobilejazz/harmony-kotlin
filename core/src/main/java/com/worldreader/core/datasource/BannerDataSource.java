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

  private BannerNetworkDataSource network;
  private BannerBdDataSource storage;
  private BannerEntityDataMapper entityDataMapper;
  private CountryCodeProvider countryCodeProvider;

  @Inject public BannerDataSource(BannerNetworkDataSource network, BannerBdDataSource bdDataSource,
      BannerEntityDataMapper entityDataMapper, CountryCodeProvider countryCodeProvider) {
    this.network = network;
    this.storage = bdDataSource;
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
      List<BannerEntity> bannerEntities = storage.obtains(key);
      List<Banner> banners = transform(bannerEntities);

      responseBannersLoaded(callback, banners);
    } catch (InvalidCacheException invalid) {
      network.fetchMainBanners(index, limit, new CompletionCallback<List<BannerEntity>>() {
        @Override public void onSuccess(List<BannerEntity> result) {
          storage.persist(key, result);

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
      List<BannerEntity> bannerEntities = storage.obtains(key);
      List<Banner> banners = transform(bannerEntities);

      responseBannersLoaded(callback, banners);
    } catch (InvalidCacheException invalid) {
      network.fetchCollectionBanners(index, limit, new CompletionCallback<List<BannerEntity>>() {
        @Override public void onSuccess(List<BannerEntity> result) {
          storage.persist(key, result);

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

  @Override public void getAll(String type, int index, int limit,
      final Callback<List<Banner>> callback) {
    final String key = URLProvider.withEndpoint(type)
        .addIndex(index)
        .addLimit(limit)
        .addCountryCode(countryCodeProvider.getCountryCode())
        .build();

    try {
      List<BannerEntity> bannerEntitiesFromCache = storage.obtains(key);
      List<Banner> banners = transform(bannerEntitiesFromCache);

      if (callback != null) {
        callback.onSuccess(banners);
      }
    } catch (InvalidCacheException e) {
      network.getAll(type, index, limit, new Callback<List<BannerEntity>>() {
        @Override public void onSuccess(final List<BannerEntity> bannerEntities) {
          storage.persist(key, bannerEntities);

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

  @Override public void get(int id, final String type, final Callback<Banner> callback) {
    final String key = URLProvider.withEndpoint(type)
        .addId(id)
        .addCountryCode(countryCodeProvider.getCountryCode())
        .build();

    try {
      BannerEntity bannerEntity = storage.obtain(key);
      Banner banner = entityDataMapper.transform(bannerEntity);

      if (callback != null) {
        callback.onSuccess(banner);
      }
    } catch (InvalidCacheException e) {
      network.get(id, type, new Callback<BannerEntity>() {
        @Override public void onSuccess(BannerEntity bannerEntity) {
          storage.persist(key, bannerEntity);

          Banner banner = entityDataMapper.transform(bannerEntity);

          if (callback != null) {
            callback.onSuccess(banner);
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
