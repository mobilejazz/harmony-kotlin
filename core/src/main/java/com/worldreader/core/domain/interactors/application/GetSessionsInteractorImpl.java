package com.worldreader.core.domain.interactors.application;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Action;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class GetSessionsInteractorImpl extends AbstractInteractor<List<Date>, ErrorCore<?>> implements GetSessionsInteractor {

  private static final String TAG = GetSessionsInteractor.class.getSimpleName();

  private final Action<Void, List<Date>> getSessionsAction;
  private final Logger logger;

  private DomainCallback<List<Date>, ErrorCore<?>> callback;
  private DomainBackgroundCallback<List<Date>, ErrorCore<?>> backgroundCallback;

  @Inject
  public GetSessionsInteractorImpl(final InteractorExecutor executor, final MainThread mainThread, final Action<Void, List<Date>> getSessionsAction,
      final Logger logger) {
    super(executor, mainThread);
    this.getSessionsAction = getSessionsAction;
    this.logger = logger;
  }

  @Override public void execute(DomainCallback<List<Date>, ErrorCore<?>> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void execute(DomainBackgroundCallback<List<Date>, ErrorCore<?>> backgroundCallback) {
    this.backgroundCallback = backgroundCallback;
    this.executor.run(this);
  }

  @Override public void run() {
    List<Date> sessions = getSessionsAction.perform(null);
    logger.d(TAG, "Number of sessions: " + sessions.size());
    if (backgroundCallback != null) {
      backgroundCallback.onSuccess(sessions);
      backgroundCallback = null;
    } else {
      performSuccessCallback(callback, sessions);
      callback = null;
    }
  }
}
