package com.worldreader.core.datasource.network.datasource.book;

import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.model.BookMetadataEntity;
import com.worldreader.core.datasource.model.StreamingResourceEntity;
import com.worldreader.core.domain.model.BookImageQuality;
import org.javatuples.Pair;

import java.io.*;

public interface StreamingBookNetworkDataSource {

  String ENDPOINT = "/books";

  void retrieveBookMetadata(final String bookId, final String version, final Callback<Pair<BookMetadataEntity, InputStream>> callback);

  StreamingResourceEntity getBookResource(final String id, final BookMetadataEntity bookMetadata, final String resource,
      final BookImageQuality bookImageQuality) throws Exception;

}
