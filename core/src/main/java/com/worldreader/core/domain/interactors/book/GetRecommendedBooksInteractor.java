package com.worldreader.core.domain.interactors.book;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.Category;

import java.util.*;
import java.util.concurrent.*;

public interface GetRecommendedBooksInteractor {

  void execute(int offset, int limit, Book book, DomainCallback<List<Book>, ErrorCore> callback);

  ListenableFuture<Optional<List<Book>>> execute(Book book, int offset, int limit, Executor executor);

  ListenableFuture<Optional<List<Book>>> execute(final Book book, final int offset, final int limit);

  ListenableFuture<Optional<List<Book>>> execute(Book book, int offset, int limit, String language, Executor executor);

  void execute(int offset, int limit, Book book, DomainCallback<List<Book>, ErrorCore> callback, Set<Category> rootCategories);

  ListenableFuture<Optional<List<Book>>> execute(final Book book, final int offset, final int limit, Set<Category> rootCategories);

  ListenableFuture<Optional<List<Book>>> execute(Book book, int offset, int limit, String language, Executor executor, Set<Category> rootCategories);
}
