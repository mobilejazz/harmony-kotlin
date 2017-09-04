package com.worldreader.core.domain.interactors.user;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.worldreader.core.concurrency.SafeCallable;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.model.user.User2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class GetUserCategoriesInteractor {

  private final GetUserInteractor getUserInteractor;
  private final ListeningExecutorService executorService;

  @Inject public GetUserCategoriesInteractor(final GetUserInteractor getUserInteractor,
      final ListeningExecutorService executorService) {
    this.getUserInteractor = getUserInteractor;
    this.executorService = executorService;
  }

  public ListenableFuture<List<Integer>> execute() {
    return executorService.submit(new SafeCallable<List<Integer>>() {
      @Override protected List<Integer> safeCall() throws Throwable {
        final UserStorageSpecification spec = new UserStorageSpecification(
            UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);
        final User2 user = getUserInteractor.execute(spec, MoreExecutors.directExecutor()).get();

        final List<String> categories =
            Collections.unmodifiableList(Lists.newArrayList(user.getFavoriteCategories()));

        return new ToIntegerListFunction().apply(categories);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        // Nothing to do
      }
    });
  }

  private static class ToIntegerListFunction implements Function<List<String>, List<Integer>> {

    @Override public List<Integer> apply(List<String> input) {
      Preconditions.checkNotNull(input, "input == null");
      final List<Integer> result = Lists.newArrayList();
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
