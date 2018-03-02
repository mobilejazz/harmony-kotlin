package com.worldreader.core.domain.interactors.reader;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;

import java.util.*;
import java.util.concurrent.*;

public interface AddBookDownloadedInteractor {

  void execute(String bookId, String bookVersion, Date time, DomainCallback<Boolean, ErrorCore<?>> callback);

  ListenableFuture<Boolean> execute(String bookId, String bookVersion);

  ListenableFuture<Boolean> execute(String bookId, String bookVersion, Executor executor);
}
