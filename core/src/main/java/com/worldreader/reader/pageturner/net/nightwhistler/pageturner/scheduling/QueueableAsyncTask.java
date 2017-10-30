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
import jedi.option.Option;

import static java.lang.Integer.toHexString;
import static jedi.option.Options.none;

/**
 * Subclass of AsyncTask which notifies the scheduler when it's done.
 */
public class QueueableAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Option<Result>> {

  private QueueCallback callback;
  private boolean cancelRequested = false;

  public void doOnCancelled(Option<Result> result) {
  }

  public void doOnPostExecute(Option<Result> result) {
  }

  @Override public Option<Result> doInBackground(Params... params) {
    return none();
  }

  @Override protected final void onPostExecute(Option<Result> result) {
    if (callback != null) {
      callback.onTaskCompleted(this, cancelRequested, result);
    }
    doOnPostExecute(result);
  }

  public void setOnCompletedCallback(QueueCallback callback) {
    this.callback = callback;
  }

  public QueueCallback getCallback() {
    return callback;
  }

  @Override protected final void onCancelled(Option<Result> result) {
    if (callback != null) {
      callback.onTaskCompleted(this, this.cancelRequested, result);
    }
    doOnCancelled(result);
  }

  @Override protected final void onCancelled() {
    onCancelled(null);
  }

  public void requestCancellation() {
    this.cancelRequested = true;
    this.cancel(true);
  }

  @Override public String toString() {
    return getClass().getSimpleName() + " (" + toHexString(hashCode()) + ")";
  }

  public interface QueueCallback {

    void onTaskCompleted(QueueableAsyncTask<?, ?, ?> task, boolean canceled, Option<?> result);
  }

  public interface Action {

    void perform();
  }
}
