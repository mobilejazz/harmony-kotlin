package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Book;

import java.util.*;

public interface GetBooksDownloadedFullInformationInteractor {

  void execute(DomainCallback<List<Book>, ErrorCore<?>> callback);

}
