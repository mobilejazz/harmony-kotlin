package com.worldreader.core.datasource.storage.datasource.book;

import com.worldreader.core.datasource.model.BookEntity;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

import java.util.*;

public interface BookBdDataSource {

  List<BookEntity> obtains(String key) throws InvalidCacheException;

  BookEntity obtain(String key) throws InvalidCacheException;

  void persist(String key, List<BookEntity> books);

  void persist(String key, BookEntity book);

  void delete(String key);
}
