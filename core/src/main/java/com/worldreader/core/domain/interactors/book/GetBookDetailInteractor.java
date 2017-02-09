package com.worldreader.core.domain.interactors.book;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Book;

import java.util.concurrent.*;

public interface GetBookDetailInteractor {

  void execute(String bookId, DomainCallback<Book, ErrorCore<?>> callback);

  void execute(String bookId, boolean forceUpdate, DomainCallback<Book, ErrorCore<?>> callback);

  void execute(String bookId, boolean forceUpdate,
      DomainBackgroundCallback<Book, ErrorCore<?>> callback);

  ListenableFuture<Optional<Book>> execute(String bookId);

  ListenableFuture<Optional<Book>> execute(String bookId, Executor executor);
}
