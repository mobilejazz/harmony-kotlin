package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling;

import android.util.Log;
import jedi.functional.Command;
import jedi.option.Option;

import java.util.*;

import static jedi.option.Options.none;
import static jedi.option.Options.some;

/**
 * Generic task scheduling queue.
 * <p>
 * Allows for consistent execution and cancelling of tasks
 * across Android versions.
 *
 * @author Alex Kuiper
 */
public class TaskQueue {

  private static final String TAG = TaskQueue.class.getSimpleName();

  private final LinkedList<QueuedTask<?, ?, ?>> taskQueue = new LinkedList<>();

  @SafeVarargs public final <A, B, C> void executeTask(QueueableAsyncTask<A, B, C> task, A... parameters) {
    final QueueableAsyncTask.QueueCallback originalCallback = task.getCallback();
    task.setOnCompletedCallback(new InnerQueueCallback(originalCallback));
    taskQueue.add(new QueuedTask<>(task, parameters));
    Log.d(TAG, "Scheduled task of type " + task + " total tasks scheduled now: " + taskQueue.size());
    if (taskQueue.size() == 1) {
      Log.d(TAG, "Starting task " + taskQueue.peek() + " since task queue is 1.");
      taskQueue.peek().execute();
    }
  }

  public boolean isEmpty() {
    return taskQueue.isEmpty();
  }

  public void clear() {
    Log.d(TAG, "Clearing task queue.");
    if (!taskQueue.isEmpty()) {
      final QueuedTask front = taskQueue.peek();
      Log.d(TAG, "Canceling task of type: " + front);
      front.cancel();
      taskQueue.clear();
    } else {
      Log.d(TAG, "Nothing to do, since queue was already empty.");
    }
  }

  private String getQueueAsString() {
    final StringBuilder builder = new StringBuilder("[");

    for (int i = 0; i < taskQueue.size(); i++) {
      builder.append(taskQueue.get(i));
      if (i < taskQueue.size() - 1) {
        builder.append(", ");
      }
    }

    builder.append("]");

    return builder.toString();
  }

  private void taskCompleted(QueueableAsyncTask<?, ?, ?> task, boolean cancelled) {
    if (cancelled) {
      Log.d(TAG, "Got onTaskCompleted for task " + task + " which was cancelled.");
      findQueuedTaskFor(task).forEach(new Command<QueuedTask<?, ?, ?>>() {
        @Override public void execute(QueuedTask<?, ?, ?> object) {
          taskQueue.remove(object);
        }
      });
    } else {
      Log.d(TAG, "Completion of task of type " + task);
      final QueuedTask queuedTask = taskQueue.remove();
      if (queuedTask.getTask() != task) {
        final String errorMsg = "Tasks out of sync! Expected " + queuedTask.getTask() + " but got " + task + " with queue: " + getQueueAsString();
        Log.e(TAG, errorMsg);
        throw new RuntimeException(errorMsg);
      }
    }

    Log.d(TAG, "Total tasks scheduled now: " + this.taskQueue.size() + " with queue: " + getQueueAsString());

    if (!taskQueue.isEmpty()) {
      final QueuedTask<?, ?, ?> peek = taskQueue.peek();
      if (!peek.isExecuting()) {
        Log.d(TAG, "Executing task " + peek);
        peek.execute();
      } else {
        Log.d(TAG, "Task at the head of queue is already running.");
      }
    }
  }

  private Option<? extends QueuedTask<?, ?, ?>> findQueuedTaskFor(QueueableAsyncTask<?, ?, ?> task) {
    for (QueuedTask<?, ?, ?> wrapper : taskQueue) {
      if (wrapper.getTask() == task) {
        return some(wrapper);
      }
    }
    return none();
  }

  private class InnerQueueCallback implements QueueableAsyncTask.QueueCallback {

    private final QueueableAsyncTask.QueueCallback delegate;

    InnerQueueCallback(QueueableAsyncTask.QueueCallback delegate) {
      this.delegate = delegate;
    }

    @Override public void onTaskCompleted(QueueableAsyncTask<?, ?, ?> task, boolean canceled, Option<?> result) {
      if (delegate != null) {
        delegate.onTaskCompleted(task, canceled, result);
      }
      taskCompleted(task, canceled);
    }
  }
}
