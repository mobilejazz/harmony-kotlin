package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.model.user.User2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class GetUserCategoriesInteractor {

  private final InteractorHandler interactorHandler;
  private final GetUserInteractor getUserInteractor;

  @Inject public GetUserCategoriesInteractor(InteractorHandler interactorHandler,
      GetUserInteractor getUserInteractor) {
    this.interactorHandler = interactorHandler;
    this.getUserInteractor = getUserInteractor;
  }

  public ListenableFuture<List<Integer>> execute() {
    final SettableFuture<List<Integer>> future = SettableFuture.create();

    final UserStorageSpecification spec = new UserStorageSpecification(
        UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);
    final ListenableFuture<User2> getUserFuture = getUserInteractor.execute(spec);

    interactorHandler.addCallback(getUserFuture, new FutureCallback<User2>() {
      @Override public void onSuccess(User2 user) {
        final List<String> categories =
            Collections.unmodifiableList(Lists.newArrayList(user.getFavoriteCategories()));
        final List<Integer> categoriesId = new ToIntegerListFunction().apply(categories);
        future.set(categoriesId);
      }

      @Override public void onFailure(@NonNull Throwable t) {
        future.setException(t);
      }
    });

    return future;
  }

  private static class ToIntegerListFunction implements Function<List<String>, List<Integer>> {

    @Override public List<Integer> apply(List<String> input) {
      Preconditions.checkNotNull(input, "input == null");
      final List<Integer> result = Lists.newArrayList(input.size());
      for (String categoryIdString : input) {
        Integer parsedResult;
        try {
          parsedResult = Integer.valueOf(categoryIdString);
        } catch (NumberFormatException e) {
          parsedResult = null;
        }
        if (parsedResult != null) {
          result.add(parsedResult);
        }
      }
      return Collections.unmodifiableList(result);
    }
  }

}
