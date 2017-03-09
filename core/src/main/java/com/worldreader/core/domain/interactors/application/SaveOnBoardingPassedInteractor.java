package com.worldreader.core.domain.interactors.application;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;

import java.util.concurrent.*;

public interface SaveOnBoardingPassedInteractor {

  void execute(boolean status, DomainCallback<Boolean, ErrorCore> callback);

  ListenableFuture<Boolean> execute(final boolean status);

  ListenableFuture<Boolean> execute(final boolean status, final Executor executor);
}
