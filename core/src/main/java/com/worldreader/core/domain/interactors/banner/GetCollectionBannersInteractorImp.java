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

public class GetCollectionBannersInteractorImp extends AbstractInteractor<List<Banner>, ErrorCore>
    implements GetCollectionBannersInteractor {

  private BannerRepository bannerRepository;

  private int index;
  private int limit;
  private DomainCallback<List<Banner>, ErrorCore> callback;

  @Inject
  public GetCollectionBannersInteractorImp(InteractorExecutor executor, MainThread mainThread,
      BannerRepository bannerRepository) {
    super(executor, mainThread);
    this.bannerRepository = bannerRepository;
  }

  @Override
  public void execute(int index, int limit, DomainCallback<List<Banner>, ErrorCore> callback) {
    this.index = index;
    this.limit = limit;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    bannerRepository.collectionBanner(index, limit, new CompletionCallback<List<Banner>>() {
      @Override public void onSuccess(List<Banner> result) {
        performSuccessCallback(callback, result);
      }

      @Override public void onError(ErrorCore error) {
        performErrorCallback(callback, error);
      }
    });
  }
}
