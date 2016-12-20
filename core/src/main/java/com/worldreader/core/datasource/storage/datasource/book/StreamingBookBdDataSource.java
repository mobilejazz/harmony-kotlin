package com.worldreader.core.datasource.storage.datasource.book;

import com.worldreader.core.datasource.model.BookMetadataEntity;
import com.worldreader.core.datasource.model.StreamingResourceEntity;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

import java.io.*;

public interface StreamingBookBdDataSource {

  BookMetadataEntity obtainBookMetadata(String key) throws InvalidCacheException;

  StreamingResourceEntity obtainStreamingResource(String key) throws InvalidCacheException;

  void persist(String key, BookMetadataEntity bookMetadataEntity);

  InputStream persist(String key, StreamingResourceEntity streamingResourceEntity) throws IOException;

  boolean deleteStreamingResource(String key);
}
