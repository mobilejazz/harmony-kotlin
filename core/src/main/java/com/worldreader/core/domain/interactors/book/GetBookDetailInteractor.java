package com.worldreader.core.domain.interactors.book;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Book;

public interface GetBookDetailInteractor {

  void execute(String bookId, DomainCallback<Book, ErrorCore<?>> callback);

  void execute(String bookId, boolean forceUpdate, DomainCallback<Book, ErrorCore<?>> callback);

  void execute(String bookId, boolean forceUpdate,
      DomainBackgroundCallback<Book, ErrorCore<?>> callback);
}
