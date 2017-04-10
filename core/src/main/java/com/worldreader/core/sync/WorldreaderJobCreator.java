package com.worldreader.core.sync;

import android.app.Application;
import android.content.Context;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.mobilejazz.logger.library.Logger;
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
import com.worldreader.core.sync.jobs.SynchronizationJob;

import javax.inject.Inject;

public class WorldreaderJobCreator implements JobCreator {

  private final Context context;
  private final InjectableCompanion companion;

  public WorldreaderJobCreator(Application context, InjectableCompanion companion) {
    this.context = context.getApplicationContext();
    this.companion = companion;
  }

  @Override public Job create(final String tag) {
    switch (tag) {
      case SynchronizationJob.TAG:
        return new SynchronizationJob(context, companion);
      default:
        return null;
    }
  }

  public static void scheduleAllJobs() {
    SynchronizationJob.scheduleJob();
  }

  public static void cancelAllJobs() {
    SynchronizationJob.cancelJob();
  }

  public static class InjectableCompanion {
    public @Inject Logger logger;
    public @Inject GetUserInteractor getUserInteractor;
    public @Inject SaveUserInteractor saveUserInteractor;
    public @Inject GetAllUserBookInteractor getAllUserBookInteractor;
    public @Inject PutAllUserBooksInteractor putAllUserBooksInteractor;
    public @Inject UserScoreSynchronizationProcessInteractor userScoreSynchronizationProcessInteractor;
    public @Inject GetUnsyncUserMilestonesInteractor getUnsyncUserMilestonesInteractor;
    public @Inject PutAllUserMilestonesNetworkInteractor putAllUserMilestonesNetworkInteractor;
    public @Inject PutAllUserMilestonesInteractor putAllUserMilestonesInteractor;
    public @Inject GetAllUserBookLikesInteractor getAllUserBookLikesInteractor;
    public @Inject PutAllUserBooksLikesInteractor putAllUserBooksLikesInteractor;
  }

}
