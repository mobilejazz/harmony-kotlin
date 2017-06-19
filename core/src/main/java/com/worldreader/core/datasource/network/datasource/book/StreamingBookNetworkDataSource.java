package com.worldreader.core.datasource.network.datasource.book;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.datasource.model.BookMetadataEntity;
import com.worldreader.core.datasource.model.StreamingResourceEntity;

public interface StreamingBookNetworkDataSource {

  String ENDPOINT = "/books";

  void retrieveBookMetadata(final String bookId,
      final CompletionCallback<BookMetadataEntity> callback);

  StreamingResourceEntity getBookResource(final String id, final BookMetadataEntity bookMetadata, final String resource) throws Exception;
}
