/*
 * Copyright (C) 2013 Alex Kuiper
 *
 * This file is part of PageTurner
 *
 * PageTurner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PageTurner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PageTurner.  If not, see <http://www.gnu.org/licenses/>.*
 */

package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling;

import android.os.AsyncTask;
import jedi.functional.Command;
import jedi.functional.Functor;
import jedi.option.Option;

import static java.lang.Integer.toHexString;
import static jedi.option.Options.none;

/**
 * Subclass of AsyncTask which notifies the scheduler when it's done.
 */
public class QueueableAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Option<Result>> {

  private Action onPreExecutionOperation;
  private Command<Option<Result>> onPostExecuteOperation;
  private Command<Option<Result>> onCancelledOperation;
  private Command<Progress[]> onProgressUpdateOperation;
  private Functor<Params[], Option<Result>> doInBackgroundFunction;
  private QueueCallback callback;
  private boolean cancelRequested = false;

  /**
   * Called before execution.
   */
  public void doOnPreExecute() {
    if (this.onPostExecuteOperation != null) {
      this.onPreExecutionOperation.perform();
    }
  }

  public void doOnProgressUpdate(Progress... values) {
    if (this.onProgressUpdateOperation != null) {
      this.onProgressUpdateOperation.execute(values);
    }
  }

  /**
   * Called when a cancellation is requested.
   * <p>
   * Default simply sets a flag and calls cancel()
   */
  public void requestCancellation() {
    this.cancelRequested = true;
    this.cancel(true);
  }

  public boolean isCancelRequested() {
    return cancelRequested;
  }

  public void doOnCancelled(Option<Result> result) {
    if (this.onCancelledOperation != null) {
      this.onCancelledOperation.execute(result);
    }
  }

  public void setCallback(QueueCallback callback) {
    this.callback = callback;
  }

  /**
   * Gets executed on the UI thread.
   * <p>
   * Override this to implement your on post-processing operations.
   */
  public void doOnPostExecute(Option<Result> result) {
    if (this.onPostExecuteOperation != null) {
      this.onPostExecuteOperation.execute(result);
    }
  }

  @Override public Option<Result> doInBackground(Params... paramses) {
    if (this.doInBackgroundFunction != null) {
      return this.doInBackgroundFunction.execute(paramses);
    }

    return none();
  }

  @Override protected final void onPreExecute() {
    this.doOnPreExecute();
  }

  /**
   * Overridden and made final to implement notification.
   * <p>
   * Subclasses should override doOnPostExecute() instead.
   */
  @Override protected final void onPostExecute(Option<Result> result) {
    if (callback != null) {
      callback.taskCompleted(this, this.cancelRequested);
    }

    doOnPostExecute(result);
  }

  @SafeVarargs @Override protected final void onProgressUpdate(Progress... values) {
    this.doOnProgressUpdate(values);
  }

  @Override protected final void onCancelled(Option<Result> result) {
    if (callback != null) {
      callback.taskCompleted(this, this.cancelRequested);
    }

    doOnCancelled(result);
  }

  @Override protected final void onCancelled() {
    onCancelled(null);
  }

  @Override public String toString() {
    return getClass().getSimpleName() + " (" + toHexString(hashCode()) + ")";
  }

  public interface QueueCallback {

    void taskCompleted(QueueableAsyncTask<?, ?, ?> task, boolean wasCancelled);
  }

  public interface Action {

    void perform();
  }
}
