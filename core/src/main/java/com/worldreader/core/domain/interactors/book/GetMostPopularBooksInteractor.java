package com.worldreader.core.domain.interactors.book;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.Category;

import java.util.*;

public interface GetMostPopularBooksInteractor {

  void execute(int offset, int limit, List<Category> categories,
      DomainCallback<List<Book>, ErrorCore> callback);

}
