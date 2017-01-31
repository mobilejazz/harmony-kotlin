package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.model.BookDownloaded;

import java.util.*;

public interface GetBooksDownloadedInteractor {

  void execute(DomainBackgroundCallback<List<BookDownloaded>, ErrorCore<?>> callback);

}
