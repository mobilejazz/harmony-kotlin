package com.worldreader.core.domain.interactors.user;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.domain.interactors.user.userbooks.RemoveUserBookInteractor;
import com.worldreader.core.domain.model.user.UserBook;

import javax.inject.Inject;

public class RemoveUserBookCurrentlyReadingInteractor {

  private final ListeningExecutorService executorService;
  private final RemoveUserBookInteractor removeUserBookInteractor;

  @Inject
  public RemoveUserBookCurrentlyReadingInteractor(final ListeningExecutorService executorService,
      final RemoveUserBookInteractor removeUserBookInteractor) {
    this.executorService = executorService;
    this.removeUserBookInteractor = removeUserBookInteractor;
  }

  public ListenableFuture<Optional<UserBook>> execute(final String bookId) {
    return removeUserBookInteractor.execute(bookId, executorService);
  }
}
