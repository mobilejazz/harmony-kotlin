package com.worldreader.core.domain.interactors.reader;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.model.BookDownloaded;

import java.util.*;
import java.util.concurrent.*;

public interface GetBooksDownloadedInteractor {

  void execute(DomainBackgroundCallback<List<BookDownloaded>, ErrorCore<?>> callback);

  ListenableFuture<Optional<List<BookDownloaded>>> execute();

  ListenableFuture<Optional<List<BookDownloaded>>> execute(final Executor executor);

}
