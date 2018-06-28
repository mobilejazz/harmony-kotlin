package com.worldreader.core.domain.interactors.book;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.Category;

import java.util.*;
import java.util.concurrent.*;

public interface GetLatestBooksInteractor {

  void execute(DomainCallback<List<Book>, ErrorCore> callback);

  void execute(int index, int limit, DomainCallback<List<Book>, ErrorCore> callback);

  ListenableFuture<List<Book>>execute(final int offset, final int limit, final List<Category> categories, final Executor executor);
}
