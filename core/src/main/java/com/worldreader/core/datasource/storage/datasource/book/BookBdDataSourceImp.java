package com.worldreader.core.datasource.storage.datasource.book;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mobilejazz.vastra.ValidationService;
import com.worldreader.core.datasource.model.BookEntity;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBddDataSource;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheObject;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

import javax.inject.Inject;
import java.util.*;

public class BookBdDataSourceImp implements BookBdDataSource {

  private CacheBddDataSource cacheBddDataSource;
  private Gson gson;
  private ValidationService validationService;

  @Inject public BookBdDataSourceImp(CacheBddDataSource cacheBddDataSource, Gson gson,
      ValidationService validationService) {
    this.cacheBddDataSource = cacheBddDataSource;
    this.gson = gson;
    this.validationService = validationService;
  }

  @Override public List<BookEntity> obtains(String key) throws InvalidCacheException {
    CacheObject cacheObject = cacheBddDataSource.get(key);

    if (cacheObject == null) {
      throw new InvalidCacheException();
    }

    List<BookEntity> bookEntities = getBookEntities(cacheObject);

    for (BookEntity bookEntity : bookEntities)
      if (!validationService.isValid(bookEntity)) {
        throw new InvalidCacheException();
      }

    return bookEntities;
  }

  @Override public BookEntity obtain(String key) throws InvalidCacheException {
    CacheObject cacheObject = cacheBddDataSource.get(key);

    if (cacheObject == null) {
      throw new InvalidCacheException();
    }

    BookEntity bookEntity = getBookEntity(cacheObject);

    if (!validationService.isValid(bookEntity)) {
      throw new InvalidCacheException();
    }

    return bookEntity;
  }

  @Override public void persist(String key, List<BookEntity> books) {
    addLastUpdateAttributeToBooks(books);

    String json = gson.toJson(books);
    CacheObject cacheObject = CacheObject.newCacheObject(key, json, System.currentTimeMillis());
    cacheBddDataSource.persist(cacheObject);
  }

  @Override public void persist(String key, BookEntity book) {
    addLastUpdateAttributeToBooks(Arrays.asList(book));

    String json = gson.toJson(book);
    CacheObject cacheObject = CacheObject.newCacheObject(key, json, System.currentTimeMillis());
    cacheBddDataSource.persist(cacheObject);
  }

  @Override public void delete(String key) {
    cacheBddDataSource.delete(key);
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private List<BookEntity> getBookEntities(CacheObject cacheObject) {
    return gson.fromJson(cacheObject.getValue(), new TypeToken<List<BookEntity>>() {
    }.getType());
  }

  private BookEntity getBookEntity(CacheObject cacheObject) {
    return gson.fromJson(cacheObject.getValue(), new TypeToken<BookEntity>() {
    }.getType());
  }

  private void addLastUpdateAttributeToBooks(List<BookEntity> books) {
    for (BookEntity book : books) {
      if (book != null) {
        book.setLastUpdate(new Date());
      }
    }
  }

}
