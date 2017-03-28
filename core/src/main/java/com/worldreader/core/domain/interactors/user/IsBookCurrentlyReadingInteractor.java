package com.worldreader.core.domain.interactors.user;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;

public interface IsBookCurrentlyReadingInteractor {

  void execute(String bookId, DomainCallback<Boolean, ErrorCore> callback);

  ListenableFuture<Boolean> execute(String bookId);
}