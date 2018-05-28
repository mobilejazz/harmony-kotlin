package com.worldreader.core.sync;

import android.app.Application;
import android.content.Context;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.worldreader.core.sync.jobs.SynchronizationJob;

public class WorldreaderJobCreator implements JobCreator {

  private final Context context;

  public WorldreaderJobCreator(Application context) {
    this.context = context.getApplicationContext();
  }

  @Override public Job create(final String tag) {
    switch (tag) {
      case SynchronizationJob.TAG:
        return new SynchronizationJob(context);
      default:
        return null;
    }
  }

  public static void scheduleAllJobs() {
    SynchronizationJob.scheduleJob();
  }

  public static void cancelAllJobs() {
    JobManager.instance().cancelAll();
  }

}
