package com.worldreader.core.domain.interactors.userflow;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.UserFlow;
import com.worldreader.core.domain.repository.UserFlowRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.List;

public class ResetLogoutUserFlowInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>>
    implements ResetLogoutUserFlowInteractor {

  private final UserFlowRepository repository;
  private DomainBackgroundCallback<Boolean, ErrorCore<?>> callback;

  @Inject
  public ResetLogoutUserFlowInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      UserFlowRepository repository) {
    super(executor, mainThread);
    this.repository = repository;
  }

  @Override public void execute(DomainBackgroundCallback<Boolean, ErrorCore<?>> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    List<UserFlow> readerUserFlow = repository.getUserFlow(UserFlow.Type.READER);
    resetUserFlow(readerUserFlow);
    repository.update(UserFlow.Type.READER, readerUserFlow);
    if (callback != null) {
      callback.onSuccess(true);
      callback = null;
    }
  }

  private void resetUserFlow(List<UserFlow> userFlows) {
    for (UserFlow userFlow : userFlows) {
      if (userFlow.getPhase() == UserFlow.PHASE.READER_SET_YOUR_GOALS
          || userFlow.getPhase() == UserFlow.PHASE.READER_BECOME_A_WORLDREADER) {
        // Reset the boolean that check if the userflow was displayed
        userFlow.setIsDisplayed(false);
      }
    }
  }
}
