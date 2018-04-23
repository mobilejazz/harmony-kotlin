package net.nightwhistler.pageturner.scheduling;

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
