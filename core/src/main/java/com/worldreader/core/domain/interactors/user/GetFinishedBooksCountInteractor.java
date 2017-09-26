package com.worldreader.core.domain.interactors.user;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;

import java.util.concurrent.*;

public interface GetFinishedBooksCountInteractor {

  void execute(int collectionId, DomainCallback<Integer, ErrorCore> callback);

  ListenableFuture<Integer> execute();

  ListenableFuture<Integer> execute(final Executor executor);

  ListenableFuture<Integer> execute(final int collectionId);

  ListenableFuture<Integer> execute(final int collectionId, final Executor executor);
}
