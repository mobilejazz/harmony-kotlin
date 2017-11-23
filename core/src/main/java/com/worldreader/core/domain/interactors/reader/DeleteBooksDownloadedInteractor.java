package com.worldreader.core.domain.interactors.reader;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.worldreader.core.domain.model.Book;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

public class DeleteBooksDownloadedInteractor {

  private final ListeningExecutorService executor;
  private final DeleteBookDownloadedInteractor deleteBookDownloadedInteractor;

  @Inject
  public DeleteBooksDownloadedInteractor(ListeningExecutorService executor, DeleteBookDownloadedInteractor deleteBookDownloadedInteractor) {
    this.executor = executor;
    this.deleteBookDownloadedInteractor = deleteBookDownloadedInteractor;
  }

  public ListenableFuture<Void> execute(final List<Book> books) {
    return executor.submit(new Callable<Void>() {
      @Override public Void call() throws Exception {
        for (Book book : books) {
          deleteBookDownloadedInteractor.execute(book.getId(), book.getVersion(), MoreExecutors.directExecutor()).get();
        }
        return null;
      }
    });
  }
}
