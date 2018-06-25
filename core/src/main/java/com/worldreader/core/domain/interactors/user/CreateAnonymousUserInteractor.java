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
import com.worldreader.core.domain.model.user.KidsUser;
import com.worldreader.core.domain.model.user.User2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

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

  /**
   * This method is intended for being used for the Worldreader Kids app
   * @param user
   * @return
   */
  public ListenableFuture<User2> execute(KidsUser user, @NonNull final List<Integer> favoriteCategories) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getIneractorRunnable(favoriteCategories, user, future));
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

  @NonNull SafeRunnable getIneractorRunnable(
      final @NonNull List<Integer> favoriteCategories,
      final SettableFuture<User2> future) {
    return getIneractorRunnable(favoriteCategories, null, future);

  }

  @NonNull SafeRunnable getIneractorRunnable(
      final @NonNull List<Integer> favoriteCategories,
      @Nullable final KidsUser user,
      final SettableFuture<User2> future) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final User2 anonymousUser = UserFactory.createAnonymous(favoriteCategories, user);
        future.set(anonymousUser);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    };
  }

  private static class UserFactory {

    static User2 createAnonymous(final List<Integer> favoriteCategories, @Nullable final KidsUser kidsUser) {
      final List<String> categories = toStringListCategories(favoriteCategories);
      User2.Builder userBuilder = new User2.Builder().setId(UsersTable.ANONYMOUS_USER_ID)
          .setFavoriteCategories(categories);

      if (kidsUser != null) {
        userBuilder.setName(kidsUser.getName());
        userBuilder.setChildName(kidsUser.getChildName());
        userBuilder.setAvatarId(kidsUser.getAvatarId());
        userBuilder.setChildBirthDate(kidsUser.getChildBirthDate());
        userBuilder.setChildGender(kidsUser.getChildGender().getValue());
        userBuilder.setRelationship(kidsUser.getRelationship());
      }

      return userBuilder.build();
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
