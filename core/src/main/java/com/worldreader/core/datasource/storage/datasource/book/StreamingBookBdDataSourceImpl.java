package com.worldreader.core.datasource.storage.datasource.book;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.datasource.model.BookMetadataEntity;
import com.worldreader.core.datasource.model.StreamingResourceEntity;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBddDataSource;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBookBddDataSource;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheBytes;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheObject;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;
import com.worldreader.core.datasource.storage.general.compress.Bytes;
import com.worldreader.core.datasource.storage.general.compress.Deflaters;

import javax.inject.Inject;
import java.io.*;
import java.util.zip.*;

public class StreamingBookBdDataSourceImpl implements StreamingBookBdDataSource {

  private final CacheBddDataSource cacheBddDataSource;
  private final CacheBookBddDataSource cacheBookBddDataSource;
  private final Gson gson;
  private final Logger logger;

  @Inject public StreamingBookBdDataSourceImpl(CacheBddDataSource cacheBddDataSource,
      CacheBookBddDataSource cacheBookBddDataSource, Gson gson, Logger logger) {
    this.cacheBddDataSource = cacheBddDataSource;
    this.cacheBookBddDataSource = cacheBookBddDataSource;
    this.gson = gson;
    this.logger = logger;
  }

  @Override public BookMetadataEntity obtainBookMetadata(String key) throws InvalidCacheException {
    CacheObject cacheObject = cacheBddDataSource.get(key);

    if (cacheObject == null) {
      throw new InvalidCacheException();
    }

    return getBookMetadataEntity(cacheObject);
  }

  @Override public StreamingResourceEntity obtainStreamingResource(String key) throws InvalidCacheException {
    final CacheBytes cacheBytes = cacheBookBddDataSource.get(key);

    if (cacheBytes == null) {
      return null;
    } else {
      try {
        final byte[] bytes = cacheBytes.getValue();
        final byte[] decompressedBytes = Deflaters.decompress(bytes);
        final InputStream inputStream = new ByteArrayInputStream(decompressedBytes);
        return StreamingResourceEntity.create(inputStream);
      } catch (IOException | DataFormatException e) {
        return null;
      }
    }
  }

  @Override public void persist(String key, BookMetadataEntity bookMetadataEntity) {
    String json = gson.toJson(bookMetadataEntity);
    CacheObject cacheObject = CacheObject.newCacheObject(key, json, System.currentTimeMillis());
    cacheBddDataSource.persist(cacheObject);
  }

  @Override public InputStream persist(String key, StreamingResourceEntity streamingResourceEntity) throws IOException {
    byte[] bytes = Bytes.toByteArray(streamingResourceEntity.getInputStream());

    final CacheBytes cacheBytes = CacheBytes.create(key, Deflaters.compress(bytes), System.currentTimeMillis());

    cacheBookBddDataSource.persist(cacheBytes);

    // OKHttp buffer consumes the stream, is only one shot, so we returned here in order to avoid querying again to the DDBB
    return new ByteArrayInputStream(bytes);
  }

  @Override public boolean deleteStreamingResource(String key) {
    cacheBookBddDataSource.delete(key);
    return true;
  }

  private BookMetadataEntity getBookMetadataEntity(CacheObject cacheObject) {
    return gson.fromJson(cacheObject.getValue(), new TypeToken<BookMetadataEntity>() {
    }.getType());
  }
}
