package com.worldreader.core.domain.interactors.book;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.datasource.helper.Action;
import com.worldreader.core.domain.model.Book;

import javax.inject.Inject;
import java.util.concurrent.*;

@PerActivity public class SaveBookCurrentlyReadingInteractor {

  private final ListeningExecutorService executor;
  private final Action<Book, Boolean> putBookReadingAction;

  @Inject public SaveBookCurrentlyReadingInteractor(ListeningExecutorService executor, Action<Book, Boolean> action) {
    this.executor = executor;
    this.putBookReadingAction = action;
  }

  public ListenableFuture<Boolean> execute(final Book book) {
    return executor.submit(new Callable<Boolean>() {
      @Override public Boolean call() throws Exception {
        return putBookReadingAction.perform(book);
      }
    });
  }

}
