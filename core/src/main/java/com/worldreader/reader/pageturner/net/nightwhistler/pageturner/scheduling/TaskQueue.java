package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling;

import android.util.Log;
import java.util.LinkedList;
import jedi.functional.Command;
import jedi.option.Option;

import static jedi.option.Options.none;
import static jedi.option.Options.some;

/**
 * Generic task scheduling queue.
 *
 * Allows for consistent execution and cancelling of tasks
 * across Android versions.
 *
 * @author Alex Kuiper
 */
public class TaskQueue {

  private static final String TAG = TaskQueue.class.getSimpleName();

  private LinkedList<QueuedTask<?, ?, ?>> taskQueue = new LinkedList<QueuedTask<?, ?, ?>>();
  private TaskQueueListener listener;

  public boolean isEmpty() {
    return taskQueue.isEmpty();
  }

  @SafeVarargs public final <A, B, C> void executeTask(QueueableAsyncTask<A, B, C> task, A... parameters) {
    task.setCallback(new QueueableAsyncTask.QueueCallback() {
      @Override public void taskCompleted(QueueableAsyncTask<?, ?, ?> task1, boolean wasCancelled) {
        TaskQueue.this.taskCompleted(task1, wasCancelled);
      }
    });

    this.taskQueue.add(new QueuedTask<>(task, parameters));

    Log.d(TAG, "Scheduled task of type " + task + " total tasks scheduled now: " + this.taskQueue.size());

    if (this.taskQueue.size() == 1) {
      Log.d(TAG, "Starting task " + taskQueue.peek() + " since task queue is 1.");
      this.taskQueue.peek().execute();
    }
  }

  public void clear() {
    Log.d(TAG, "Clearing task queue.");

    if (!this.taskQueue.isEmpty()) {
      QueuedTask front = taskQueue.peek();
      Log.d(TAG, "Canceling task of type: " + front);

      front.cancel();
      this.taskQueue.clear();
    } else {
      Log.d(TAG, "Nothing to do, since queue was already empty.");
    }
  }

  public void setTaskQueueListener(TaskQueueListener listener) {
    this.listener = listener;
  }

  private String getQueueAsString() {
    StringBuilder builder = new StringBuilder("[");

    for (int i = 0; i < this.taskQueue.size(); i++) {
      builder.append(this.taskQueue.get(i));

      if (i < this.taskQueue.size() - 1) {
        builder.append(", ");
      }
    }

    builder.append("]");

    return builder.toString();
  }

  private Option<? extends QueuedTask<?, ?, ?>> findQueuedTaskFor(QueueableAsyncTask<?, ?, ?> task) {
    for (QueuedTask<?, ?, ?> wrapper : this.taskQueue) {
      if (wrapper.getTask() == task) {
        return some(wrapper);
      }
    }

    return none();
  }

  public void taskCompleted(QueueableAsyncTask<?, ?, ?> task, boolean wasCancelled) {

    if (!wasCancelled) {
      Log.d(TAG, "Completion of task of type " + task);

      QueuedTask queuedTask = this.taskQueue.remove();

      if (queuedTask.getTask() != task) {

        String errorMsg = "Tasks out of sync! Expected " + queuedTask.getTask() + " but got " + task + " with queue: " + getQueueAsString();
        Log.e(TAG, errorMsg);

        throw new RuntimeException(errorMsg);
      }
    } else {
      Log.d(TAG, "Got taskCompleted for task " + task + " which was cancelled.");

      findQueuedTaskFor(task).forEach(new Command<QueuedTask<?, ?, ?>>() {
        @Override public void execute(QueuedTask<?, ?, ?> object) {
          TaskQueue.this.taskQueue.remove(object);
        }
      });
    }

    Log.d(TAG, "Total tasks scheduled now: " + this.taskQueue.size() + " with queue: " + getQueueAsString());

    if (!this.taskQueue.isEmpty()) {

      if (!this.taskQueue.peek().isExecuting()) {
        Log.d(TAG, "Executing task " + this.taskQueue.peek());
        this.taskQueue.peek().execute();
      } else {
        Log.d(TAG, "Task at the head of queue is already running.");
      }
    } else if (this.listener != null) {
      Log.d(TAG, "Notifying that the queue is empty.");
      this.listener.queueEmpty();
    }
  }

  public interface TaskQueueListener {

    void queueEmpty();
  }
}
