package com.worldreader.core.domain.interactors.book;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Book;

public interface SaveBookCurrentlyReadingInteractor {

  void execute(Book book, DomainCallback<Boolean, ErrorCore<?>> callback);
}
