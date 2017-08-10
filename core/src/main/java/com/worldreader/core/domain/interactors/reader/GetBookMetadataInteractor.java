package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.BookMetadata;

public interface GetBookMetadataInteractor {

  void execute(final String bookId, final String version, final DomainCallback<BookMetadata, ErrorCore<?>> callback);

  void execute(final String bookId, final String version, final DomainBackgroundCallback<BookMetadata, ErrorCore<?>> callback);

  void execute(final String bookId, final String version, final boolean forceRefreshBookMetadata, final DomainBackgroundCallback<BookMetadata, ErrorCore<?>> callback);
}
