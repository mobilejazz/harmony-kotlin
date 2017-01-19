package com.worldreader.core.datasource.network.datasource.book;

import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.datasource.model.BookEntity;
import com.worldreader.core.domain.model.BookSort;

import java.util.*;

public interface BookNetworkDataSource {

  void fetchBooks(int index, int limit, List<BookSort> sorters, String list,
      List<Integer> categories, boolean countryOpen, String language,
      CompletionCallback<List<BookEntity>> callback);

  void fetchSearchBooksByTitle(int index, int limit, String title,
      CompletionCallback<List<BookEntity>> callback);

  void fetchSearchBooksByAuthor(int index, int limit, String author,
      CompletionCallback<List<BookEntity>> callback);

  void search(int index, int limit, List<Integer> categories, String title, String author,
      Callback<List<BookEntity>> callback);

  void fetchBookDetail(String bookId, String version, CompletionCallback<BookEntity> callback);

  void fetchRecommendedBooks(CompletionCallback<List<BookEntity>> callback);
}
