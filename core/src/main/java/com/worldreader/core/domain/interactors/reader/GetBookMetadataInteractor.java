package com.worldreader.core.domain.interactors.reader;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.model.BookMetadata;

import java.util.concurrent.*;

public interface GetBookMetadataInteractor {

  void execute(final String bookId, final String version, final DomainBackgroundCallback<BookMetadata, ErrorCore<?>> callback);

  void execute(final String bookId, final String version, final boolean forceRefreshBookMetadata,
      final DomainBackgroundCallback<BookMetadata, ErrorCore<?>> callback);

  //void execute(String bookId, boolean forceRefreshBookMetadata, DomainBackgroundCallback<BookMetadata, ErrorCore<?>> callback);

  ListenableFuture<BookMetadata> execute(final String bookId, final String version);

  ListenableFuture<BookMetadata> execute(final String bookId, final String version, final boolean forceRefreshBookMetadata, final Executor executor);
}
