package com.worldreader.core.domain.interactors.reader;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;

import java.util.concurrent.*;

public interface DeleteBookDownloadedInteractor {

  void execute(final String bookId, final String version, final DomainCallback<Boolean, ErrorCore<?>> callback);

  void execute(final String bookId, final String version, final DomainBackgroundCallback<Boolean, ErrorCore<?>> callback);

  ListenableFuture<Void> execute(final String bookId, final String version);

  ListenableFuture<Void> execute(String bookId, String version, Executor executor);
}
