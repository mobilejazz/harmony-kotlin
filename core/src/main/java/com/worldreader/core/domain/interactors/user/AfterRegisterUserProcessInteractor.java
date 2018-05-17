package com.worldreader.core.domain.interactors.user;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.analytics.providers.amazon.interactor.PinpointConfigAnalyticsUserIdInteractor;
import com.worldreader.core.common.date.Dates;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.spec.NetworkSpecification;
import com.worldreader.core.datasource.spec.milestones.GetAllUserMilestoneStorageSpec;
import com.worldreader.core.datasource.spec.milestones.PutUserMilestonesStorageSpec;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.datasource.spec.userbooks.GetAllUserBooksStorageSpec;
import com.worldreader.core.datasource.spec.userbooks.PutAllUserBooksStorageSpec;
import com.worldreader.core.datasource.spec.userbooks.UserBookNetworkSpecification;
import com.worldreader.core.datasource.spec.userbooks.UserBookStorageSpecification;
import com.worldreader.core.datasource.spec.userbookslike.GetAllUserBooksLikesNotSyncStorageSpec;
import com.worldreader.core.datasource.spec.userbookslike.PutAllUserBookLikeStorageSpec;
import com.worldreader.core.domain.interactors.application.SaveOnBoardingPassedInteractor;
import com.worldreader.core.domain.interactors.application.SaveSessionInteractor;
import com.worldreader.core.domain.interactors.user.milestones.CreateUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.milestones.GetAllUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.milestones.PutAllUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.milestones.PutAllUserMilestonesNetworkInteractor;
import com.worldreader.core.domain.interactors.user.score.AnonymousUserScoreSynchronizationProcessInteractor;
import com.worldreader.core.domain.interactors.user.score.UserScoreSynchronizationProcessInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.GetAllUserBookInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.PutAllUserBooksInteractor;
import com.worldreader.core.domain.interactors.user.userbookslike.GetAllUserBookLikesInteractor;
import com.worldreader.core.domain.interactors.user.userbookslike.PutAllUserBooksLikesInteractor;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.model.user.UserBookLike;
import com.worldreader.core.domain.model.user.UserMilestone;
import com.worldreader.core.error.user.UserNotFoundException;
import com.worldreader.core.sync.WorldreaderJobCreator;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class AfterRegisterUserProcessInteractor {

  private final ListeningExecutorService executor;
  private final SaveUserInteractor saveUserInteractor;
  private final GetUserInteractor getUserInteractor;
  private final DeleteUserInteractor deleteUserInteractor;
  private final GetAllUserBookInteractor getAllUserBookInteractor;
  private final GetAllUserBookLikesInteractor getAllUserBookLikesInteractor;
  private final GetAllUserMilestonesInteractor getAllUserMilestonesInteractor;
  private final UpdateUserGoalsInteractor updateUserGoalsInteractor;
  private final PutAllUserMilestonesNetworkInteractor putAllUserMilestonesNetworkInteractor;
  private final PutAllUserMilestonesInteractor putAllUserMilestonesInteractor;
  private final CreateUserMilestonesInteractor createUserMilestonesInteractor;
  private final PutAllUserBooksInteractor putAllUserBooksInteractor;
  private final PutAllUserBooksLikesInteractor putAllUserBooksLikesInteractor;
  private final SaveUserCategoriesInteractor saveUserCategoriesInteractor;
  private final UserScoreSynchronizationProcessInteractor userScoreSynchronizationProcessInteractor;
  private final AnonymousUserScoreSynchronizationProcessInteractor anonymousUserScoreSynchronizationProcessInteractor;
  private final SaveOnBoardingPassedInteractor saveOnBoardingPassedInteractor;
  private final SaveSessionInteractor saveSessionInteractor;
  private final Dates dateUtils;
  private final PinpointConfigAnalyticsUserIdInteractor configAnalyticsUserIdInteractor;

  @Inject public AfterRegisterUserProcessInteractor(final ListeningExecutorService executor, final SaveUserInteractor saveUserInteractor,
      final GetUserInteractor getUserInteractor, final DeleteUserInteractor deleteUserInteractor,
      final GetAllUserBookInteractor getAllUserBookInteractor, final GetAllUserBookLikesInteractor getAllUserBookLikesInteractor,
      final GetAllUserMilestonesInteractor getAllUserMilestonesInteractor, final UpdateUserGoalsInteractor updateUserGoalsInteractor,
      final PutAllUserMilestonesNetworkInteractor putAllUserMilestonesNetworkInteractor,
      final PutAllUserMilestonesInteractor putAllUserMilestonesInteractor, final CreateUserMilestonesInteractor createUserMilestonesInteractor,
      final PutAllUserBooksInteractor putAllUserBooksInteractor, final PutAllUserBooksLikesInteractor putAllUserBooksLikesInteractor,
      final SaveUserCategoriesInteractor saveUserCategoriesInteractor,
      final UserScoreSynchronizationProcessInteractor userScoreSynchronizationProcessInteractor,
      final AnonymousUserScoreSynchronizationProcessInteractor anonymousUserScoreSynchronizationProcessInteractor,
      final SaveOnBoardingPassedInteractor saveOnBoardingPassedInteractor, final SaveSessionInteractor saveSessionInteractor, final Dates dateUtils,
      final PinpointConfigAnalyticsUserIdInteractor configAnalyticsUserIdInteractor) {
    this.executor = executor;
    this.saveUserInteractor = saveUserInteractor;
    this.getUserInteractor = getUserInteractor;
    this.deleteUserInteractor = deleteUserInteractor;
    this.getAllUserBookInteractor = getAllUserBookInteractor;
    this.getAllUserBookLikesInteractor = getAllUserBookLikesInteractor;
    this.getAllUserMilestonesInteractor = getAllUserMilestonesInteractor;
    this.updateUserGoalsInteractor = updateUserGoalsInteractor;
    this.putAllUserMilestonesNetworkInteractor = putAllUserMilestonesNetworkInteractor;
    this.putAllUserMilestonesInteractor = putAllUserMilestonesInteractor;
    this.createUserMilestonesInteractor = createUserMilestonesInteractor;
    this.putAllUserBooksInteractor = putAllUserBooksInteractor;
    this.putAllUserBooksLikesInteractor = putAllUserBooksLikesInteractor;
    this.saveUserCategoriesInteractor = saveUserCategoriesInteractor;
    this.userScoreSynchronizationProcessInteractor = userScoreSynchronizationProcessInteractor;
    this.anonymousUserScoreSynchronizationProcessInteractor = anonymousUserScoreSynchronizationProcessInteractor;
    this.saveOnBoardingPassedInteractor = saveOnBoardingPassedInteractor;
    this.saveSessionInteractor = saveSessionInteractor;
    this.dateUtils = dateUtils;
    this.configAnalyticsUserIdInteractor = configAnalyticsUserIdInteractor;
  }

  public enum From {
    ONBOARDING, EVERYTHING_ELSE
  }

  public ListenableFuture<User2> execute(final From from, final User2 user2, final List<Integer> categoriesId, final boolean isAnonymous) {
    final SettableFuture<User2> future = SettableFuture.create();
    executor.execute(getInteractorRunnable(future, from, user2, categoriesId, isAnonymous));
    return future;
  }

  @NonNull SafeRunnable getInteractorRunnable(final SettableFuture<User2> future, final From from, final User2 user, final List<Integer> categoriesId,
      final boolean isAnonymous) {
    return new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        handleNewUserRegisteredFromEverythingElseProcess(future, user, isAnonymous);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }

      private void handleNewUserRegisteredFromEverythingElseProcess(final SettableFuture<User2> future, final User2 user, final boolean isAnonymous)
          throws Throwable {
        // 0 - Save new user
        saveUserInteractor.execute(user, isAnonymous ? SaveUserInteractor.Type.ANONYMOUS : SaveUserInteractor.Type.LOGGED_IN,
            MoreExecutors.directExecutor()).get();

        if (isAnonymous) {
          final List<UserMilestone> milestones =
              createUserMilestonesInteractor.execute(user.getId(), Collections.<Integer>emptyList(), MoreExecutors.directExecutor()).get();
          final PutUserMilestonesStorageSpec putUserMilestonesStorageSpec =
              new PutUserMilestonesStorageSpec(UserStorageSpecification.UserTarget.ANONYMOUS);
          putAllUserMilestonesInteractor.execute(putUserMilestonesStorageSpec, milestones, MoreExecutors.directExecutor());
          saveOnBoardingPassedAndSaveSessionPassed(future, user);
          return;
        }

        // 1 - Get anonymous user
        final UserStorageSpecification getAnonymousUserSpec = new UserStorageSpecification(UserStorageSpecification.UserTarget.ANONYMOUS);
        final ListenableFuture<User2> getAnonymousUserFuture = getUserInteractor.execute(getAnonymousUserSpec, MoreExecutors.directExecutor());

        try {
          final User2 anonymousUser = getAnonymousUserFuture.get();

          //// 2 - If there's no user then there's no point to continue checking the rest
          //if (anonymousUser == null) {
          //  saveUserInteractor.execute(user, isAnonymous ? SaveUserInteractor.Type.ANONYMOUS : SaveUserInteractor.Type.LOGGED_IN).get();
          //  saveOnBoardingPassedAndSaveSessionPassed(future, user);
          //  return;
          //}

          // 3 - If there's anonymous user, get pagesPerDay information and send it to the server
          final int pagesPerDay = anonymousUser.getPagesPerDay();
          final int minChildAge = anonymousUser.getMinChildAge();
          final int maxChildAge = anonymousUser.getMaxChildAge();

          final ListenableFuture<User2> updateUserGoalsFuture =
              updateUserGoalsInteractor.execute(pagesPerDay, minChildAge, maxChildAge, MoreExecutors.directExecutor());

          updateUserGoalsFuture.get();

          // 4 - Get anonymous userbooks
          final GetAllUserBooksStorageSpec getAllUserBooksStorageSpec = new GetAllUserBooksStorageSpec();
          final ListenableFuture<Optional<List<UserBook>>> getAnonymousUserBooksFuture =
              getAllUserBookInteractor.execute(getAllUserBooksStorageSpec, MoreExecutors.directExecutor());
          final List<UserBook> anonymousUserBooks = getAnonymousUserBooksFuture.get().or(Collections.<UserBook>emptyList());

          // 5 - If we have userbooks, then we have to send it
          if (!anonymousUserBooks.isEmpty()) {
            UserBookNetworkSpecification userBookNetworkSpecification = new UserBookNetworkSpecification();
            final ListenableFuture<Optional<List<UserBook>>> putAllUserBooksFuture =
                putAllUserBooksInteractor.execute(userBookNetworkSpecification, anonymousUserBooks, MoreExecutors.directExecutor());
            final List<UserBook> updatedUserBooks = putAllUserBooksFuture.get().or(Collections.<UserBook>emptyList());

            // 5.1 - If we have updated userbooks, store it for the current user
            if (!updatedUserBooks.isEmpty()) {
              final UserBookStorageSpecification updatedUserBookStorageSpecification = new PutAllUserBooksStorageSpec();
              putAllUserBooksInteractor.execute(updatedUserBookStorageSpecification, updatedUserBooks, MoreExecutors.directExecutor()).get();
            }
          }

          // Get UserBooksLikes
          final GetAllUserBooksLikesNotSyncStorageSpec getAllUserBooksLikesNotSyncStorageSpec =
              new GetAllUserBooksLikesNotSyncStorageSpec(UserStorageSpecification.UserTarget.ANONYMOUS);
          final List<UserBookLike> userBookLikes =
              getAllUserBookLikesInteractor.execute(getAllUserBooksLikesNotSyncStorageSpec, MoreExecutors.directExecutor()).get();

          if (userBookLikes != null && !userBookLikes.isEmpty()) {
            // If we have userbooks, then we send it to server
            final List<UserBookLike> updatedUserBooksLike =
                putAllUserBooksLikesInteractor.execute(userBookLikes, new NetworkSpecification(), MoreExecutors.directExecutor()).get();

            // After everything OK, then we store it for the current user
            putAllUserBooksLikesInteractor.execute(updatedUserBooksLike,
                new PutAllUserBookLikeStorageSpec(UserStorageSpecification.UserTarget.LOGGED_IN), MoreExecutors.directExecutor()).get();
          }

          // 6 - Get anonymous completed milestones
          final GetAllUserMilestoneStorageSpec getAllUserMilestoneStorageSpec = new GetAllUserMilestoneStorageSpec();
          final ListenableFuture<Optional<List<UserMilestone>>> getAllUserMilestonesFuture =
              getAllUserMilestonesInteractor.execute(getAllUserMilestoneStorageSpec, MoreExecutors.directExecutor());
          final List<UserMilestone> anonymousUserMilestones = getAllUserMilestonesFuture.get().or(Collections.<UserMilestone>emptyList());

          // 7 - If we have milestones, then we have to send it to the server
          if (!anonymousUserMilestones.isEmpty()) {
            final ListenableFuture<List<UserMilestone>> updateMilestonesFuture =
                putAllUserMilestonesNetworkInteractor.execute(anonymousUserMilestones, MoreExecutors.directExecutor());

            // Ignore this result as we're going to update the user later
            updateMilestonesFuture.get();
          }

          // 8 - Launch userscore process to sync the score
          userScoreSynchronizationProcessInteractor.execute(MoreExecutors.directExecutor()).get();
          //anonymousUserScoreSynchronizationProcessInteractor.execute(anonymousUser.getId(), user.getId(), MoreExecutors.directExecutor()).get();

          // 9 - Save markInMyBooks categories
          final List<String> favoriteCategories = anonymousUser.getFavoriteCategories();
          if (favoriteCategories != null && !favoriteCategories.isEmpty()) {
            final List<Integer> categoriesTransformed = Lists.newArrayListWithCapacity(favoriteCategories.size());
            for (final String categoryString : favoriteCategories) {
              categoriesTransformed.add(Integer.valueOf(categoryString));
            }
            saveUserCategoriesInteractor.execute(categoriesTransformed, MoreExecutors.directExecutor());
          }

          // 10 - Get the new updated user
          final NetworkSpecification userNetworkSpec = new NetworkSpecification();
          final ListenableFuture<User2> getUpdatedUserFuture = getUserInteractor.execute(userNetworkSpec, MoreExecutors.directExecutor());
          final User2 newUpdatedUser = getUpdatedUserFuture.get();

          // 11 - Generate new UserMilestones for the new user
          final String id = newUpdatedUser.getId();
          final List<Integer> rawUpdatedUserMilestones = newUpdatedUser.getMilestones();
          final ListenableFuture<List<UserMilestone>> newUserMilestonesFuture =
              createUserMilestonesInteractor.execute(id, rawUpdatedUserMilestones, MoreExecutors.directExecutor());
          final List<UserMilestone> updatedUserMilestones = newUserMilestonesFuture.get();

          // 12 - Save updated user
          final ListenableFuture<User2> newUpdatedUserFuture =
              saveUserInteractor.execute(newUpdatedUser, SaveUserInteractor.Type.LOGGED_IN, MoreExecutors.directExecutor());
          newUpdatedUserFuture.get();

          // 13 - Save updated user milestones
          final PutUserMilestonesStorageSpec putUserMilestonesStorageSpec =
              new PutUserMilestonesStorageSpec(UserStorageSpecification.UserTarget.LOGGED_IN);
          final ListenableFuture<Optional<List<UserMilestone>>> updateUserMilestonesFuture =
              putAllUserMilestonesInteractor.execute(putUserMilestonesStorageSpec, updatedUserMilestones, MoreExecutors.directExecutor());
          updateUserMilestonesFuture.get();

          // 14 - Delete anonymous user
          deleteUserInteractor.execute(DeleteUserInteractor.Type.ANONYMOUS, MoreExecutors.directExecutor()).get();

          // 15 - SaveOnBoarding
          saveOnBoardingPassedAndSaveSessionPassed(future, newUpdatedUser);

          // 16 - Config the user id
          configAnalyticsUserIdInteractor.execute(newUpdatedUser, MoreExecutors.directExecutor()).get();

        } catch (Throwable e) {

          // Means that there user came from onboarding process, so there is no anonymous user.
          if (e.getCause() instanceof UserNotFoundException) {

            // Update the user categories.
            if (categoriesId != null && categoriesId.size() > 0) {
              final User2 userWithCategories = saveUserCategoriesInteractor.execute(categoriesId, MoreExecutors.directExecutor()).get();

              // Update the new user with the category ids.
              saveUserInteractor.execute(userWithCategories, SaveUserInteractor.Type.LOGGED_IN, MoreExecutors.directExecutor()).get();
            }

            // generate the user milestones.
            final String id = getUserInteractor.execute(MoreExecutors.directExecutor()).get().getId();
            final List<UserMilestone> userMilestones =
                createUserMilestonesInteractor.execute(id, Collections.<Integer>emptyList(),
                    MoreExecutors.directExecutor()).get();

            final PutUserMilestonesStorageSpec putUserMilestonesStorageSpec =
                new PutUserMilestonesStorageSpec(UserStorageSpecification.UserTarget.LOGGED_IN);
            putAllUserMilestonesInteractor.execute(putUserMilestonesStorageSpec, userMilestones, MoreExecutors.directExecutor()).get();

            // Save onboarding and finishe the user.
            saveOnBoardingPassedAndSaveSessionPassed(future, user);
            return;
          } else {
            throw e;
          }
        }
      }

      private void saveOnBoardingPassedAndSaveSessionPassed(final SettableFuture<User2> future, final User2 user) {
        // 1.- Save that the user has passed the onboarding
        final ListenableFuture<Boolean> saveOnBoardingPassedFuture = saveOnBoardingPassedInteractor.execute(true, MoreExecutors.directExecutor());

        // 2.- Save the session
        final ListenableFuture<Boolean> saveSessionFuture = Futures.transformAsync(saveOnBoardingPassedFuture, new AsyncFunction<Boolean, Boolean>() {
          @Override public ListenableFuture<Boolean> apply(@Nullable final Boolean input) throws Exception {
            return saveSessionInteractor.execute(dateUtils.today());
          }
        }, MoreExecutors.directExecutor());

        // 3.- After that just set user to finally complete the process
        Futures.addCallback(saveSessionFuture, new FutureCallback<Boolean>() {
          @Override public void onSuccess(@Nullable final Boolean result) {
            WorldreaderJobCreator.scheduleAllJobs();
            future.set(user);
          }

          @Override public void onFailure(final Throwable t) {
            future.setException(t);
          }
        });
      }
    };
  }

}
