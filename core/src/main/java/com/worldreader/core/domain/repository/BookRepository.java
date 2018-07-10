package com.worldreader.core.domain.repository;

import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookSort;

import java.util.*;

public interface BookRepository {

  String KEY_LIST_FEATURED = "featured";
  String KEY_LATEST = "latest";

  void books(
      List<Integer> categoriesId,
      String list,
      List<BookSort> sorters,
      boolean openCountry,
      String language,
      int index,
      int limit, CompletionCallback<List<Book>> callback
  );

  void search(
      int index,
      int limit,
      List<Integer> categories,
      String title,
      String author,
      String publisher,
      List<String> languages,
      List<String> ages,
      Callback<List<Book>> callback
  );

  void bookDetail(
      String bookId,
      String version,
      boolean forceUpdate,
      CompletionCallback<Book> callback
  );

  void bookDetailLatest(String bookId, boolean forceUpdate, CompletionCallback<Book> callback);
}
