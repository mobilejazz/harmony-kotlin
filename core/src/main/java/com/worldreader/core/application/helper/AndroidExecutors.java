package com.worldreader.core.application.helper;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.*;

public class AndroidExecutors {

  private static final AndroidExecutors INSTANCE = new AndroidExecutors();

  private final Executor UI_THREAD;

  private AndroidExecutors() {
    this.UI_THREAD = new UIThreadExecutor();
  }

  public static Executor uiThread() {
    return INSTANCE.UI_THREAD;
  }

  private static class UIThreadExecutor implements Executor {

    private final Handler handler;

    public UIThreadExecutor() {
      this.handler = new Handler(Looper.getMainLooper());
    }

    @Override public void execute(@NonNull Runnable command) {
      handler.post(command);
    }
  }

}