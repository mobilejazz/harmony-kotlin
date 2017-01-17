package com.worldreader.core.domain.interactors.book;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Book;

import java.util.*;

public interface SearchBookByAuthorInteractor {

  void execute(int index, int limit, String query, DomainCallback<List<Book>, ErrorCore> callback);

  ListenableFuture<Optional<List<Book>>> execute(String query, int index, int limit);
}
