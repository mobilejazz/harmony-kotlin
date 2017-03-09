package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.util.concurrent.FutureCallback;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.interactors.user.userbooks.UnlikeBookInteractor;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;

public class UserUnLikeBookInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>>
    implements UserUnLikeBookInteractor {

  private final InteractorHandler interactorHandler;
  private final UnlikeBookInteractor unlikeBookInteractor;

  private String bookId;

  private DomainCallback<Boolean, ErrorCore<?>> callback;

  @Inject public UserUnLikeBookInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      InteractorHandler interactorHandler, UnlikeBookInteractor unlikeBookInteractor) {
    super(executor, mainThread);
    this.interactorHandler = interactorHandler;
    this.unlikeBookInteractor = unlikeBookInteractor;
  }

  @Override public void execute(String bookId, DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    interactorHandler.addCallback(unlikeBookInteractor.execute(bookId),
        new FutureCallback<UserBook>() {
          @Override public void onSuccess(UserBook userBookLike) {
            performSuccessCallback(callback, userBookLike != null);
          }

          @Override public void onFailure(@NonNull Throwable t) {
            performErrorCallback(callback, ErrorCore.of(t));
          }
        });
  }
}
