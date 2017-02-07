package com.worldreader.core.domain.interactors.reader;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;

public interface DeleteBookDownloadedInteractor {

  void execute(String bookId, DomainCallback<Boolean, ErrorCore<?>> callback);

  void execute(String bookId, DomainBackgroundCallback<Boolean, ErrorCore<?>> callback);

  ListenableFuture<Void> execute(final String bookId);
}
