package com.worldreader.core.domain.interactors.user;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.*;

@Deprecated public interface EstablishUserGoalsInteractor {

  ListenableFuture<Boolean> execute();

  ListenableFuture<Boolean> execute(final Executor executor);
}
