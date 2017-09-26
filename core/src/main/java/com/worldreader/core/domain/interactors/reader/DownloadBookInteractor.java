package com.worldreader.core.domain.interactors.reader;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;

import java.util.concurrent.*;

public interface DownloadBookInteractor {

  void execute(String bookId, DomainCallback<Integer, ErrorCore<?>> callback);

  void execute(String bookId, boolean forceBookMetadataRefresh, DomainBackgroundCallback<Void, ErrorCore<?>> callback);

  void execute(final String bookId, final String version, final DomainCallback<Integer, ErrorCore<?>> callback);

  ListenableFuture<Void> execute(final String bookId, final String version, final Executor executor);
}
