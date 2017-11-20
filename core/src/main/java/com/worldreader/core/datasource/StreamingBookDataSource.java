package com.worldreader.core.datasource;

import com.google.common.util.concurrent.ListenableFuture;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.helper.url.URLProvider;
import com.worldreader.core.datasource.mapper.BookMetadataEntityDataMapper;
import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.BookMetadataEntity;
import com.worldreader.core.datasource.model.StreamingResourceEntity;
import com.worldreader.core.datasource.network.datasource.book.StreamingBookNetworkDataSource;
import com.worldreader.core.datasource.storage.datasource.book.StreamingBookBdDataSource;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;
import com.worldreader.core.domain.model.BookImageQuality;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.StreamingResource;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueuedTask;
import org.javatuples.Pair;

import javax.inject.Inject;
import java.io.*;
import java.util.concurrent.*;

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

  @Override public void retrieveBookMetadata(String bookId, String version, boolean forceRefreshBookMetadata, Callback<BookMetadata> callback) {
    final String key = URLProvider.withEndpoint(StreamingBookNetworkDataSource.ENDPOINT)
        .addId(bookId)
        .addVersion(StreamingBookRepository.KEY_LATEST)
        .addSubPath(StreamingBookRepository.CONTENT_OPF_LOCATION_PATH)
        .build();

    if (forceRefreshBookMetadata) {
      fetchBookMetadata(key, version, bookId, callback);
    } else {
      try {
        final BookMetadataEntity cached = bddDataSource.obtainBookMetadata(key);
        final BookMetadata response = transform(cached);
        notifyResponse(response, callback);
      } catch (InvalidCacheException e) {
        fetchBookMetadata(key, version, bookId, callback);
      }
    }
  }

  @Override public BookMetadata getBookMetadata(String bookId) {
    final String key = URLProvider.withEndpoint(StreamingBookNetworkDataSource.ENDPOINT)
        .addId(bookId)
        .addVersion(StreamingBookRepository.KEY_LATEST)
        .addSubPath(StreamingBookRepository.CONTENT_OPF_LOCATION_PATH)
        .build();

    try {
      final BookMetadataEntity cached = bddDataSource.obtainBookMetadata(key);
      return transform(cached);
    } catch (InvalidCacheException e) {
      return null;
    }
  }

  @Override public StreamingResource getBookResource(String id, BookMetadata bookMetadata, String resource) throws Exception {
    return getBookResource(id, bookMetadata, resource, BookImageQuality.NETWORK_QUALITY_DEPENDANT);
  }

  @Override public StreamingResource getBookResource(String id, BookMetadata bookMetadata, String resource, final BookImageQuality bookImageQuality) throws
      Exception {
    final String key = id + resource;

    StreamingResourceEntity streamingResourceEntity = bddDataSource.obtainStreamingResource(key);

    if (streamingResourceEntity == null) {
      streamingResourceEntity = networkDataSource.getBookResource(id, transformInverse(bookMetadata), resource, bookImageQuality);

      try {
        final StreamingResourceEntity savedStreamingResourceEntity = StreamingResourceEntity.create(bddDataSource.persist(key, streamingResourceEntity));
        return streamingResourceMapper.transform(savedStreamingResourceEntity);
      } catch (IOException e) {
        logger.e(TAG, "Error while trying to save resource to database for book id: " + id + " exception: " + e);
        return StreamingResource.EMPTY;
      }
    }

    return streamingResourceMapper.transform(streamingResourceEntity);
  }

  @Override
  public ListenableFuture<StreamingResource> getBookResourceFuture(final String id, final BookMetadata bookMetadata, final String resource) {
    return QueuedTask.READER_THREAD_EXECUTOR.submit(new Callable<StreamingResource>() {
      @Override public StreamingResource call() throws Exception {
        return getBookResource(id, bookMetadata, resource);
      }
    });
  }

  @Override public StreamingResource getBookResourceFastAccess(String id, String resource) {
    final String key = id + resource;
    final StreamingResourceEntity streamingResourceEntity = bddDataSource.obtainStreamingResource(key);

    return streamingResourceEntity == null ? null : streamingResourceMapper.transform(streamingResourceEntity);
  }

  @Override public boolean deleteBookResource(String bookId, String resource) {
    final String key = bookId + resource;
    return bddDataSource.deleteStreamingResource(key);
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private void fetchBookMetadata(final String key, final String version, final String bookId, final Callback<BookMetadata> callback) {
    networkDataSource.retrieveBookMetadata(bookId, version, new Callback<Pair<BookMetadataEntity, InputStream>>() {

      @Override public void onSuccess(Pair<BookMetadataEntity, InputStream> pair) {
        final BookMetadataEntity bookMetadataEntity = pair.getValue0();
        final InputStream contentOpfIs = pair.getValue1();

        bddDataSource.persist(key, bookMetadataEntity);

        // NOTE: Optimization, we store also the content.opf file to avoid having duplicate network calls later. Later access will reuse the cache
        final String contentOpfKey = bookId + bookMetadataEntity.getContentOpfName();
        try {
          bddDataSource.persist(contentOpfKey, StreamingResourceEntity.create(contentOpfIs));
        } catch (IOException e) {
          // If something went wrong, just ignore it (it will be stored on next access with page turner)
          e.printStackTrace();
        }

        final BookMetadata transformed = transform(bookMetadataEntity);
        notifyResponse(transformed, callback);
      }

      @Override public void onError(Throwable e) {
        if (callback != null) {
          callback.onError(e);
        }
      }
    });
  }

  private void notifyResponse(BookMetadata response, Callback<BookMetadata> callback) {
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
