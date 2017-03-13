package com.worldreader.core.domain.interactors.userflow;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.UserFlow;
import com.worldreader.core.domain.thread.MainThread;

import java.util.List;

public class BaseUserFlowInteractor extends AbstractInteractor<List<UserFlow>, ErrorCore<?>> {

  public BaseUserFlowInteractor(InteractorExecutor executor, MainThread mainThread) {
    super(executor, mainThread);
  }

  @Override public void run() {
    // Nothing to do, It's overridden on the child classes
  }

  protected UserFlow getUserFlow(int phase, List<UserFlow> userFlows) {
    for (UserFlow userFlow : userFlows) {
      if (userFlow.getPhase() == phase) {
        return userFlow;
      }
    }

    throw new IllegalStateException("Phase not found");
  }
}
