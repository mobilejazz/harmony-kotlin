package com.worldreader.core.domain.interactors.user.userbooks;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.datasource.spec.userbookslike.GetUserBookLikeStorageSpec;
import com.worldreader.core.domain.model.user.UserBookLike;
import com.worldreader.core.domain.repository.UserBooksLikeRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class IsBookLikedInteractor {

  private final ListeningExecutorService executor;
  private final UserBooksLikeRepository repository;

  @Inject
  public IsBookLikedInteractor(ListeningExecutorService executor, UserBooksLikeRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Boolean> execute(final String bookId) {
    final SettableFuture<Boolean> future = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        Preconditions.checkNotNull(bookId, "bookId == null");

        final GetUserBookLikeStorageSpec spec = new GetUserBookLikeStorageSpec(UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);
        spec.setBookId(bookId);
        repository.get(spec, new Callback<Optional<UserBookLike>>() {
          @Override public void onSuccess(final Optional<UserBookLike> userBookOptional) {
            if (userBookOptional.isPresent()) {
              future.set(userBookOptional.get().isLiked());
            } else {
              future.set(false);
            }
          }

          @Override public void onError(final Throwable e) {
            future.setException(e);
          }
        });
      }
    });

    return future;
  }

}
