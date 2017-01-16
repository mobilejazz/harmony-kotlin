package com.worldreader.core.domain.interactors.book;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Book;

import java.util.*;

public interface SearchBookByTitleInteractor {

  void execute(int index, int limit, String query, DomainCallback<List<Book>, ErrorCore> callback);
}
