package com.worldreader.core.datasource;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.url.URLProvider;
import com.worldreader.core.datasource.mapper.BookMetadataEntityDataMapper;
import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.BookMetadataEntity;
import com.worldreader.core.datasource.model.StreamingResourceEntity;
import com.worldreader.core.datasource.network.datasource.book.StreamingBookNetworkDataSource;
import com.worldreader.core.datasource.storage.datasource.book.StreamingBookBdDataSource;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.StreamingResource;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import javax.inject.Inject;

public class StreamingBookDataSource implements StreamingBookRepository {

  private final StreamingBookNetworkDataSource networkDataSource;
  private final StreamingBookBdDataSource bddDataSource;
  private final BookMetadataEntityDataMapper bookMetadataEntityDataMapper;
  private final Mapper<StreamingResource, StreamingResourceEntity> streamingResourceMapper;
  private final Logger logger;

  @Inject public StreamingBookDataSource(StreamingBookNetworkDataSource networkDataSource, StreamingBookBdDataSource bddDataSource,
      BookMetadataEntityDataMapper bookMetadataEntityDataMapper, Mapper<StreamingResource, StreamingResourceEntity> streamingResourceMapper,
      Logger logger) {
    this.networkDataSource = networkDataSource;
    this.bddDataSource = bddDataSource;
    this.bookMetadataEntityDataMapper = bookMetadataEntityDataMapper;
    this.streamingResourceMapper = streamingResourceMapper;
    this.logger = logger;
  }

  @Override public void retrieveBookMetadata(String bookId, boolean forceRefreshBookMetadata, CompletionCallback<BookMetadata> callback) {
    final String key = URLProvider.withEndpoint(StreamingBookNetworkDataSource.ENDPOINT)
        .addId(bookId)
        .addVersion(StreamingBookRepository.KEY_LATEST)
        .addSubPath(StreamingBookRepository.CONTENT_OPF_LOCATION_PATH)
        .build();

    if (forceRefreshBookMetadata) {
      fetchBookMetadata(key, bookId, callback);
    } else {
      try {
        BookMetadataEntity cached = bddDataSource.obtainBookMetadata(key);
        BookMetadata response = transform(cached);
        notifyResponse(response, callback);
      } catch (InvalidCacheException e) {
        fetchBookMetadata(key, bookId, callback);
      }
    }
  }

  @Override public BookMetadata getBookMetadata(String bookId) {
    String key = URLProvider.withEndpoint(StreamingBookNetworkDataSource.ENDPOINT)
        .addId(bookId)
        .addVersion(StreamingBookRepository.KEY_LATEST)
        .addSubPath(StreamingBookRepository.CONTENT_OPF_LOCATION_PATH)
        .build();

    try {
      BookMetadataEntity cached = bddDataSource.obtainBookMetadata(key);
      return transform(cached);
    } catch (InvalidCacheException e) {
      return null;
    }
  }

  @Override
  public StreamingResource getBookResource(String id, BookMetadata bookMetadata, String resource)
      throws Exception {
    String key = id + resource;
    StreamingResourceEntity streamingResourceEntity = bddDataSource.obtainStreamingResource(key);

    if (streamingResourceEntity == null) {
      streamingResourceEntity = networkDataSource.getBookResource(id, transformInverse(bookMetadata), resource);

      try {
        final StreamingResourceEntity savedStreamingResourceEntity =
            StreamingResourceEntity.create(bddDataSource.persist(key, streamingResourceEntity));
        return streamingResourceMapper.transform(savedStreamingResourceEntity);
      } catch (IOException e) {
        logger.e(TAG, "Error while trying to save resource to database for book id: " + id + " exception: " + e);
        return StreamingResource.EMPTY;
      }

    }

    return streamingResourceMapper.transform(streamingResourceEntity);
  }

  static final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

  @Override public ListenableFuture<StreamingResource> getBookResourceFuture(final String id, final BookMetadata bookMetadata, final String resource) {
    return executorService.submit(new Callable<StreamingResource>() {
      @Override public StreamingResource call() throws Exception {
        return getBookResource(id, bookMetadata, resource);
      }
    });
  }

  @Override public StreamingResource getBookResourceFastAccess(String id, String resource) {
    String key = id + resource;
    final StreamingResourceEntity streamingResourceEntity = bddDataSource.obtainStreamingResource(key);

    return streamingResourceEntity == null ? null : streamingResourceMapper.transform(streamingResourceEntity);
  }

  @Override public boolean deleteBookResource(String bookId, String resource) {
    String key = bookId + resource;
    return bddDataSource.deleteStreamingResource(key);
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private void fetchBookMetadata(final String key, final String bookId, final CompletionCallback<BookMetadata> callback) {
    networkDataSource.retrieveBookMetadata(bookId, new CompletionCallback<BookMetadataEntity>() {
      @Override public void onSuccess(BookMetadataEntity bookMetadataEntity) {
        bddDataSource.persist(key, bookMetadataEntity);

        BookMetadata transformed = transform(bookMetadataEntity);
        notifyResponse(transformed, callback);
      }

      @Override public void onError(ErrorCore errorCore) {
        if (callback != null) {
          callback.onError(errorCore);
        }
      }
    });
  }

  private void notifyResponse(BookMetadata response, CompletionCallback<BookMetadata> callback) {
    if (callback != null) {
      callback.onSuccess(response);
    }
  }

  private BookMetadata transform(BookMetadataEntity result) {
    return bookMetadataEntityDataMapper.transform(result);
  }

  private BookMetadataEntity transformInverse(BookMetadata bookMetadata) {
    return bookMetadataEntityDataMapper.transformInverse(bookMetadata);
  }
}
