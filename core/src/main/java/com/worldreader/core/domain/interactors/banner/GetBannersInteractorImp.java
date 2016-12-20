package com.worldreader.core.domain.interactors.banner;

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

public class GetBannersInteractorImp extends AbstractInteractor<List<Banner>, ErrorCore>
    implements GetBannersInteractor {

  private BannerRepository bannerRepository;

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
