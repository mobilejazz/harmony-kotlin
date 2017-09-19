package com.worldreader.core.domain.interactors.banner;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Banner;

import java.util.List;
import java.util.concurrent.Executor;

public interface GetBannersInteractor {

  enum Type {
    MAIN,
    COLLECTION
  }

  void execute(Type type, int index, int limit, DomainCallback<List<Banner>, ErrorCore> callback);

  ListenableFuture<Optional<List<Banner>>> execute(String identifier, int index, int limit);

  ListenableFuture<Optional<List<Banner>>> execute(String identifier, int index, int limit, Executor executor);

}
