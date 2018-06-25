package com.worldreader.core.domain.interactors.reader;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.BookImageQuality;

import java.util.concurrent.*;

public interface DownloadBookInteractor {

  //void execute(String bookId, DomainCallback<Integer, ErrorCore<?>> callback);
  //
  void execute(String bookId, String version, boolean forceBookMetadataRefresh, DomainBackgroundCallback<Void, ErrorCore<?>> callback);

  void execute(final String bookId, final String version, final DomainCallback<Integer, ErrorCore<?>> callback);

  ListenableFuture<Void> execute(final String bookId, final String version, BookImageQuality bookImageQuality, final Executor executor);

  ListenableFuture<Void> execute(final String bookId, final String version, BookImageQuality bookImageQuality);
}