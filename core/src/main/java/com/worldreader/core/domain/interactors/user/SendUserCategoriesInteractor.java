package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.domain.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class SendUserCategoriesInteractor {

  private final ListeningExecutorService executor;
  private final UserRepository repository;

  @Inject public SendUserCategoriesInteractor(final ListeningExecutorService executor,
      final UserRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Boolean> execute(@NonNull final List<Integer> categories) {
    SettableFuture<Boolean> future = SettableFuture.create();

    return future;
  }

}
