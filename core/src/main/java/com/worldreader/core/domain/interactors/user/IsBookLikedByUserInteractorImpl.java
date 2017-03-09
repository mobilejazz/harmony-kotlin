package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.util.concurrent.FutureCallback;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.interactors.user.userbooks.IsBookLikedInteractor;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;

public class IsBookLikedByUserInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>>
    implements IsBookLikedByUserInteractor {

  private final InteractorHandler interactorHandler;
  private final IsBookLikedInteractor isBookLikedInteractor;

  private DomainCallback<Boolean, ErrorCore<?>> callback;
  private String bookId;

  @Inject public IsBookLikedByUserInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      InteractorHandler interactorHandler, IsBookLikedInteractor isBookLikedInteractor) {
    super(executor, mainThread);
    this.interactorHandler = interactorHandler;
    this.isBookLikedInteractor = isBookLikedInteractor;
  }

  @Override public void execute(String bookId, DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.callback = callback;
    this.bookId = bookId;
    this.executor.run(this);
  }

  @Override public void run() {
    interactorHandler.addCallback(isBookLikedInteractor.execute(bookId),
        new FutureCallback<Boolean>() {
          @Override public void onSuccess(Boolean result) {
            performSuccessCallback(callback, result);
          }

          @Override public void onFailure(@NonNull Throwable t) {
            performErrorCallback(callback, ErrorCore.of(t));
          }
        });
  }
}
