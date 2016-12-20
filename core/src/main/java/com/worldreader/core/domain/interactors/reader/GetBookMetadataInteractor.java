package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.BookMetadata;

public interface GetBookMetadataInteractor {

  void execute(String bookId, DomainCallback<BookMetadata, ErrorCore<?>> callback);

  void execute(String bookId, DomainBackgroundCallback<BookMetadata, ErrorCore<?>> callback);

  void execute(String bookId, boolean forceRefreshBookMetadata,
      DomainBackgroundCallback<BookMetadata, ErrorCore<?>> callback);
}
