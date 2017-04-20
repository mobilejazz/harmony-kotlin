package com.worldreader.core.domain.interactors.user;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.spec.userbooks.GetAllUserBooksCurrentlyReadingStorageSpec;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Book;

import java.util.*;
import java.util.concurrent.*;

public interface GetBooksCurrentlyReading {

  @Deprecated
  void execute(int limit, boolean allBooks, DomainCallback<List<Book>, ErrorCore> callback);

  ListenableFuture<List<Book>> execute(int limit, boolean allBooks, Executor executor);

  ListenableFuture<List<Book>> execute(final GetAllUserBooksCurrentlyReadingStorageSpec spec, final Executor executor);

  ListenableFuture<List<Book>> execute(final GetAllUserBooksCurrentlyReadingStorageSpec spec);
}
