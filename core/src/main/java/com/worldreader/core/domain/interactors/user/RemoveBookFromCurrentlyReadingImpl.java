package com.worldreader.core.domain.interactors.user;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.UserBooksRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;

public class RemoveBookFromCurrentlyReadingImpl extends AbstractInteractor<Boolean, ErrorCore>
    implements RemoveBookFromCurrentlyReading {

  private final UserBooksRepository userBooksRepository;

  private String bookId;
  private DomainCallback<Boolean, ErrorCore> callback;

  @Inject
  public RemoveBookFromCurrentlyReadingImpl(InteractorExecutor executor, MainThread mainThread,
      final UserBooksRepository userBooksRepository) {
    super(executor, mainThread);
    this.userBooksRepository = userBooksRepository;
  }

  @Override public void execute(String bookId, DomainCallback<Boolean, ErrorCore> callback) {
    this.bookId = bookId;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    userBooksRepository.unfavorite(bookId, new Callback<Optional<UserBook>>() {
      @Override public void onSuccess(final Optional<UserBook> userBookOptional) {
        if (userBookOptional.isPresent()) {
          final UserBook userBook = userBookOptional.get();

          performSuccessCallback(callback, !userBook.isFavorite());

        } else {
          performSuccessCallback(callback, false);
        }
      }

      @Override public void onError(final Throwable e) {
        performErrorCallback(callback, ErrorCore.of(e));
      }
    });
  }
}
