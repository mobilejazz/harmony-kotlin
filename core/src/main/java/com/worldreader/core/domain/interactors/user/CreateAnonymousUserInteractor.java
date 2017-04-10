package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UsersTable;
import com.worldreader.core.domain.model.user.User2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

@Singleton public class CreateAnonymousUserInteractor {

  private final ListeningExecutorService executor;

  @Inject public CreateAnonymousUserInteractor(final ListeningExecutorService executor) {
    this.executor = executor;
  }

  public ListenableFuture<User2> execute() {
    return execute(Collections.<Integer>emptyList());
  }

  public ListenableFuture<User2> execute(final Executor executor) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getIneractorRunnable(Collections.<Integer>emptyList(), future));
    return future;
  }

  public ListenableFuture<User2> execute(@NonNull final List<Integer> favoriteCategories) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getIneractorRunnable(favoriteCategories, future));
    return future;
  }

  public ListenableFuture<User2> execute(@NonNull final List<Integer> favoriteCategories,
      final Executor executor) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getIneractorRunnable(favoriteCategories, future));
    return future;
  }

  @NonNull SafeRunnable getIneractorRunnable(final @NonNull List<Integer> favoriteCategories,
      final SettableFuture<User2> future) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final User2 anonymousUser = UserFactory.createAnonymous(favoriteCategories);
        future.set(anonymousUser);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }

  private static class UserFactory {

    static User2 createAnonymous(final List<Integer> favoriteCategories) {
      final List<String> categories = toStringListCategories(favoriteCategories);
      return new User2.Builder().setId(UsersTable.ANONYMOUS_USER_ID)
          .setFavoriteCategories(categories)
          .build();
    }

    private static List<String> toStringListCategories(final List<Integer> favoriteCategories) {
      if (favoriteCategories == null) {
        return Collections.emptyList();
      }

      return Lists.newArrayList(
          Iterables.transform(favoriteCategories, new Function<Integer, String>() {
            @Nullable @Override public String apply(@Nullable final Integer input) {
              return String.valueOf(input);
            }
          }));
    }

  }
}
