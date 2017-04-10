package com.worldreader.core.sync.jobs;

import android.content.Context;
import android.support.annotation.NonNull;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.datasource.repository.spec.NetworkSpecification;
import com.worldreader.core.datasource.spec.milestones.PutUserMilestonesStorageSpec;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.datasource.spec.userbooks.GetAllUserBooksNetworkSpec;
import com.worldreader.core.datasource.spec.userbooks.GetAllUserBooksNotSychronizedStorageSpec;
import com.worldreader.core.datasource.spec.userbooks.PutAllUserBooksStorageSpec;
import com.worldreader.core.datasource.spec.userbooks.UserBookNetworkSpecification;
import com.worldreader.core.datasource.spec.userbooks.UserBookStorageSpecification;
import com.worldreader.core.datasource.spec.userbookslike.GetAllUserBooksLikesNotSyncStorageSpec;
import com.worldreader.core.datasource.spec.userbookslike.PutAllUserBookLikeStorageSpec;
import com.worldreader.core.domain.interactors.user.GetUserInteractor;
import com.worldreader.core.domain.interactors.user.SaveUserInteractor;
import com.worldreader.core.domain.interactors.user.milestones.GetUnsyncUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.milestones.PutAllUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.milestones.PutAllUserMilestonesNetworkInteractor;
import com.worldreader.core.domain.interactors.user.score.UserScoreSynchronizationProcessInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.GetAllUserBookInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.PutAllUserBooksInteractor;
import com.worldreader.core.domain.interactors.user.userbookslike.GetAllUserBookLikesInteractor;
import com.worldreader.core.domain.interactors.user.userbookslike.PutAllUserBooksLikesInteractor;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.model.user.UserBookLike;
import com.worldreader.core.domain.model.user.UserMilestone;
import com.worldreader.core.sync.WorldreaderJobCreator;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SynchronizationJob extends Job {

  public static final String TAG = "SynchronizationJob";

  private static int jobId = -1;

  private Logger logger;
  private GetUserInteractor getUserInteractor;
  private SaveUserInteractor saveUserInteractor;
  private GetAllUserBookInteractor getAllUserBookInteractor;
  private PutAllUserBooksInteractor putAllUserBooksInteractor;
  private UserScoreSynchronizationProcessInteractor userScoreSynchronizationProcessInteractor;
  private GetUnsyncUserMilestonesInteractor getUnsyncUserMilestonesInteractor;
  private PutAllUserMilestonesNetworkInteractor putAllUserMilestonesNetworkInteractor;
  private PutAllUserMilestonesInteractor putAllUserMilestonesInteractor;
  private GetAllUserBookLikesInteractor getAllUserBookLikesInteractor;
  private PutAllUserBooksLikesInteractor putAllUserBooksLikesInteractor;
  private Reachability reachability;

  public SynchronizationJob(Context context, WorldreaderJobCreator.InjectableCompanion companion) {
    this.logger = companion.logger;
    this.getUserInteractor = companion.getUserInteractor;
    this.saveUserInteractor = companion.saveUserInteractor;
    this.getAllUserBookInteractor = companion.getAllUserBookInteractor;
    this.putAllUserBooksInteractor = companion.putAllUserBooksInteractor;
    this.userScoreSynchronizationProcessInteractor = companion.userScoreSynchronizationProcessInteractor;
    this.getUnsyncUserMilestonesInteractor = companion.getUnsyncUserMilestonesInteractor;
    this.putAllUserMilestonesNetworkInteractor = companion.putAllUserMilestonesNetworkInteractor;
    this.putAllUserMilestonesInteractor = companion.putAllUserMilestonesInteractor;
    this.getAllUserBookLikesInteractor = companion.getAllUserBookLikesInteractor;
    this.putAllUserBooksLikesInteractor = companion.putAllUserBooksLikesInteractor;
    this.reachability = companion.reachability;
  }

  // Only for testing purposes
  public void execute() {
    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override public void run() {
        onRunJob(null);
      }
    });
  }

  @NonNull @Override protected Result onRunJob(final Params params) {
    logger.d(TAG, "Starting synchronization process.");

    if (!reachability.isReachable()) {
      logger.d(TAG, "Ignoring sync process as there are not internet!");
      return Result.SUCCESS;
    }

    try {
      logger.d(TAG, "Getting all the userbooks not synchronized.");
      // 1. Get all the userbooks not synchronized
      GetAllUserBooksNotSychronizedStorageSpec spec = new GetAllUserBooksNotSychronizedStorageSpec();
      final ListenableFuture<Optional<List<UserBook>>> getAllUnsychedUserBooksFuture =
          getAllUserBookInteractor.execute(spec, MoreExecutors.directExecutor());

      final List<UserBook> userBooksNotSycnhed = getAllUnsychedUserBooksFuture.get().or(Collections.<UserBook>emptyList());

      logger.d(TAG, "Userbooks not synchronized count: " + userBooksNotSycnhed.size());

      // 1.1 Check if there is userbooks to synchronize
      if (!userBooksNotSycnhed.isEmpty()) {
        logger.d(TAG, "Sending all the userbooks not synchronized.");

        // 1.1 Send all the userbooks not synchronized to the server.
        UserBookNetworkSpecification userBookNetworkSpecification = new UserBookNetworkSpecification();
        final ListenableFuture<Optional<List<UserBook>>> putAllUserBooksFuture =
            putAllUserBooksInteractor.execute(userBookNetworkSpecification, userBooksNotSycnhed, MoreExecutors.directExecutor());
        final List<UserBook> updatedUserBooks = putAllUserBooksFuture.get().or(Collections.<UserBook>emptyList());

        logger.d(TAG, "Userbooks responded by the network count:  " + updatedUserBooks.size());

        // 1.2 Check if there is userbooks updated from the network
        if (!updatedUserBooks.isEmpty()) {

          logger.d(TAG, "Updating userbooks to the storage, count:  " + updatedUserBooks.size());

          // 1.2.1 Update the userbooks in the storage.
          final UserBookStorageSpecification updatedUserBookStorageSpecification = new PutAllUserBooksStorageSpec();
          putAllUserBooksInteractor.execute(updatedUserBookStorageSpecification, updatedUserBooks, MoreExecutors.directExecutor());
        }
      }
      logger.d(TAG, "Finished user books synchronization process.");

      // 2 Synchronizing all the user score.
      logger.d(TAG, "Synchronizing the user scores");
      final ListenableFuture<Boolean> userScoreSynchronizationProcessInteractorFuture =
          userScoreSynchronizationProcessInteractor.execute(MoreExecutors.directExecutor());
      userScoreSynchronizationProcessInteractorFuture.get();
      logger.d(TAG, "Finished the user scores synchronization.");

      // 3 Synchronizing all the user milestones
      final ListenableFuture<List<UserMilestone>> getUnsyncUserMilestonesInteractorFuture =
          getUnsyncUserMilestonesInteractor.execute(MoreExecutors.directExecutor());
      final List<UserMilestone> userMilestonesNotSynched = getUnsyncUserMilestonesInteractorFuture.get();

      if (!userMilestonesNotSynched.isEmpty()) {
        // 3.1 Send all the not synchronized to the server throw the network.
        final ListenableFuture<List<UserMilestone>> putAlluserMilestoneNetworkFuture =
            putAllUserMilestonesNetworkInteractor.execute(userMilestonesNotSynched, MoreExecutors.directExecutor());
        final List<UserMilestone> userMilestonesUpdatedFromNetwork = putAlluserMilestoneNetworkFuture.get();

        // 3.2 Update the user milestone synchronized to the storage.
        final PutUserMilestonesStorageSpec putUserMilestonesStorageSpec =
            new PutUserMilestonesStorageSpec(UserStorageSpecification.UserTarget.LOGGED_IN);
        final ListenableFuture<Optional<List<UserMilestone>>> putAllUserMilestoneInteractorFuture =
            putAllUserMilestonesInteractor.execute(putUserMilestonesStorageSpec, userMilestonesUpdatedFromNetwork, MoreExecutors.directExecutor());
        putAllUserMilestoneInteractorFuture.get();
      }

      // 4 Synchronizing all userbooklikes
      final List<UserBookLike> userBookLikes =
          getAllUserBookLikesInteractor.execute(new GetAllUserBooksLikesNotSyncStorageSpec(UserStorageSpecification.UserTarget.LOGGED_IN),
              MoreExecutors.directExecutor()).get();

      if (userBookLikes != null && !userBookLikes.isEmpty()) {
        // We have to sync all userbooks like to server
        final List<UserBookLike> updatedUserBooksLikes =
            putAllUserBooksLikesInteractor.execute(userBookLikes, new NetworkSpecification(), MoreExecutors.directExecutor()).get();

        // Store updated userbooks like to database
        putAllUserBooksLikesInteractor.execute(updatedUserBooksLikes,
            new PutAllUserBookLikeStorageSpec(UserStorageSpecification.UserTarget.LOGGED_IN), MoreExecutors.directExecutor());
      }

      //----------------------------//

      logger.d(TAG, "Starting user / user books refresh information after the synchronization process.");

      // 3 Get the user from the network
      logger.d(TAG, "Getting the user from the network.");
      final ListenableFuture<User2> getUserInteractorFuture =
          this.getUserInteractor.execute(new NetworkSpecification(), MoreExecutors.directExecutor());
      final User2 user2 = getUserInteractorFuture.get();

      logger.d(TAG, "Saving the user from the network into the storage.");
      // 3.1 Saving the user from the network
      final ListenableFuture<User2> saveUserInteractorFuture =
          saveUserInteractor.execute(user2, SaveUserInteractor.Type.LOGGED_IN, MoreExecutors.directExecutor());
      saveUserInteractorFuture.get();

      // 4 Getting the user books from the server.
      logger.d(TAG, "Getting all the userbooks from the server.");

      final ListenableFuture<Optional<List<UserBook>>> getUserBooksFuture =
          getAllUserBookInteractor.execute(new GetAllUserBooksNetworkSpec(), MoreExecutors.directExecutor());
      final Optional<List<UserBook>> userBooksFromNetworkOp = getUserBooksFuture.get();
      if (userBooksFromNetworkOp.isPresent()) {
        final List<UserBook> userBooksFromNetwork = userBooksFromNetworkOp.get();

        logger.d(TAG, "Updating all the userbooks from the server into the storage.");

        // 4.1 Updating all the user books from the server to the storage.
        final PutAllUserBooksStorageSpec updateUserBooksFromNetworkSpec = new PutAllUserBooksStorageSpec();
        final ListenableFuture<Optional<List<UserBook>>> putAllUserBooksInteractorFuture =
            putAllUserBooksInteractor.execute(updateUserBooksFromNetworkSpec, userBooksFromNetwork, MoreExecutors.directExecutor());
        putAllUserBooksInteractorFuture.get();
      }

      logger.d(TAG, "Completely finish the whole process. ");

      return Result.SUCCESS;

    } catch (InterruptedException | ExecutionException e) {
      logger.e(TAG, e.toString());
      return Result.FAILURE;
    }
  }

  public static void scheduleJob() {
    jobId = new JobRequest.Builder(TAG).setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
        .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
        .setUpdateCurrent(true)
        //.setPersisted(true)
        .build()
        .schedule();
  }

  public static void cancelJob() {
    if (jobId > 0) {
      JobManager.instance().cancel(jobId);
    }
  }

}
