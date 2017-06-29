package com.worldreader.core.domain.interactors.application;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executor;

public interface SaveOnBoardingPassedInteractor {

  ListenableFuture<Boolean> execute(final boolean status);

  ListenableFuture<Boolean> execute(final boolean status, final Executor executor);
}
