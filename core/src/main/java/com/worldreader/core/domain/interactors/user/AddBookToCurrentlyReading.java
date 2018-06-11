package com.worldreader.core.domain.interactors.user;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;

import java.util.concurrent.*;

public interface AddBookToCurrentlyReading {

  void execute(String bookId, DomainCallback<Boolean, ErrorCore> callback);

  ListenableFuture<Boolean> execute(String bookId);

  ListenableFuture<Boolean> execute(String bookId, Executor executor);
}
