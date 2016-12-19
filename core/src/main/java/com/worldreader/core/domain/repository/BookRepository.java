package com.worldreader.core.domain.repository;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookSort;

import java.util.*;

public interface BookRepository {

  String KEY_LIST_FEATURED = "featured";
  String KEY_LATEST = "latest";

  void books(List<Integer> categoriesId, String list, List<BookSort> sorters, boolean openCountry,
      String language, int index, int limit, CompletionCallback<List<Book>> callback);

  void searchBooks(int index, int limit, String title, String author, String publisher,
      CompletionCallback<List<Book>> callback);

  void bookDetail(String bookId, String version, CompletionCallback<Book> callback);

  void bookDetail(String bookId, String version, boolean forceUpdate,
      CompletionCallback<Book> callback);

  void bookDetailLatest(String bookId, CompletionCallback<Book> callback);

  void bookDetailLatest(String bookId, boolean forceUpdate, CompletionCallback<Book> callback);
}
