package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.interactors.book.GetBookDetailInteractor;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.Score;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.RatingRepository;
import com.worldreader.core.domain.thread.MainThread;
import javax.inject.Inject;

public class UserLikeBookInteractorImpl extends AbstractInteractor<Double, ErrorCore> implements UserLikeBookInteractor {

  private final RatingRepository ratingRepository;
  private final GetBookDetailInteractor getBookDetailInteractor;
  private final com.worldreader.core.domain.interactors.user.userbooks.LikeBookInteractor likeBookInteractor;


  private String id;
  private Score score;
  private DomainCallback<Double, ErrorCore> callback;

  @Inject public UserLikeBookInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      RatingRepository ratingRepository, GetBookDetailInteractor getBookDetailInteractor,
      com.worldreader.core.domain.interactors.user.userbooks.LikeBookInteractor likeBookInteractor) {
    super(executor, mainThread);
    this.ratingRepository = ratingRepository;
    this.getBookDetailInteractor = getBookDetailInteractor;
    this.likeBookInteractor = likeBookInteractor;
  }

  @Override
  public void execute(String id, Score score, DomainCallback<Double, ErrorCore> callback) {
    this.id = id;
    this.score = score;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    ratingRepository.rate(id, score, new CompletionCallback<Boolean>() {
      @Override public void onSuccess(Boolean result) {
        Futures.addCallback(likeBookInteractor.execute(id),
            new FutureCallback<UserBook>() {
              @Override public void onSuccess(UserBook result) {
                getBookDetailInteractor.execute(id, true/*force update*/,
                    new DomainCallback<Book, ErrorCore<?>>(mainThread) {
                      @Override public void onSuccessResult(Book book) {
                        performSuccessCallback(callback, book.getRatings());
                      }

                      @Override public void onErrorResult(ErrorCore errorCore) {
                        performErrorCallback(callback, errorCore);
                      }
                    });
              }

              @Override public void onFailure(@NonNull Throwable t) {
                performErrorCallback(callback, ErrorCore.of(t));
              }
            });
      }

      @Override public void onError(ErrorCore error) {
        performErrorCallback(callback, error);
      }
    });
  }
}
