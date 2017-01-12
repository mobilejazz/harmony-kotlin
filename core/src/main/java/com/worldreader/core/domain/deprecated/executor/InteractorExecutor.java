package com.worldreader.core.domain.deprecated.executor;

import com.worldreader.core.domain.deprecated.Interactor;

import java.util.concurrent.*;

@Deprecated public interface InteractorExecutor {

  void run(Interactor interactor);

  Executor getExecutor();
}
