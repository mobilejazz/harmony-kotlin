package com.worldreader.core.domain.interactors.banner;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.Banner;
import com.worldreader.core.domain.repository.BannerRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

// TODO: 13/01/2017 [refactor]: Update the interactor with the new Identifier string for the banners
public class GetBannersInteractorImp extends AbstractInteractor<List<Banner>, ErrorCore>
    implements GetBannersInteractor {

  private final BannerRepository bannerRepository;

  private Type type;
  private int index;
  private int limit;
  private DomainCallback<List<Banner>, ErrorCore> callback;

  @Inject public GetBannersInteractorImp(InteractorExecutor executor, MainThread mainThread,
      BannerRepository bannerRepository) {
    super(executor, mainThread);
    this.bannerRepository = bannerRepository;
  }

  @Override public void execute(Type type, int index, int limit,
      DomainCallback<List<Banner>, ErrorCore> callback) {
    this.type = type;
    this.index = index;
    this.limit = limit;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override
  public ListenableFuture<Optional<List<Banner>>> execute(final String identifier, final int index,
      final int limit) {
    final SettableFuture<Optional<List<Banner>>> settableFuture = SettableFuture.create();

    getExecutor().execute(new Runnable() {
      @Override public void run() {
        bannerRepository.getAll(Banner.READ_TO_KIDS_BANNER_IDENTIFIER, index, limit,
            new Callback<List<Banner>>() {
              @Override public void onSuccess(List<Banner> banners) {
                settableFuture.set(Optional.fromNullable(banners));
              }

              @Override public void onError(Throwable e) {
                settableFuture.setException(e);
              }
            });
      }
    });

    return settableFuture;
  }

  @Override public void run() {
    switch (type) {
      case MAIN:
        fetchMainBanners();
        break;
      case COLLECTION:
        fetchCollectionBanners();
        break;
    }
  }

  private void fetchMainBanners() {
    bannerRepository.mainBanner(index, limit, new CompletionCallback<List<Banner>>() {
      @Override public void onSuccess(final List<Banner> result) {
        performSuccessCallback(callback, result);
      }

      @Override public void onError(final ErrorCore error) {
        performErrorCallback(callback, error);
      }
    });
  }

  private void fetchCollectionBanners() {
    bannerRepository.collectionBanner(index, limit, new CompletionCallback<List<Banner>>() {
      @Override public void onSuccess(final List<Banner> result) {
        performSuccessCallback(callback, result);
      }

      @Override public void onError(final ErrorCore error) {
        performErrorCallback(callback, error);
      }
    });
  }
}
