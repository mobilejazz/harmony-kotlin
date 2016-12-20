package com.worldreader.core.domain.interactors.book;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Book;

import java.util.*;

public interface GetLatestBooksInteractor {

  void execute(DomainCallback<List<Book>, ErrorCore> callback);

  void execute(int index, int limit, DomainCallback<List<Book>, ErrorCore> callback);
}
