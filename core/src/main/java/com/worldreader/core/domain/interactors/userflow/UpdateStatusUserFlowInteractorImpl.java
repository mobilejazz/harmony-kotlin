package com.worldreader.core.domain.interactors.userflow;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.UserFlow;
import com.worldreader.core.domain.repository.UserFlowRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class UpdateStatusUserFlowInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>>
    implements UpdateStatusUserFlowInteractor {

  private UserFlowRepository userFlowRepository;
  private UserFlow.Type type;
  private List<UserFlow> userFlows;
  private DomainCallback<Boolean, ErrorCore<?>> callback;

  @Inject
  public UpdateStatusUserFlowInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      UserFlowRepository userFlowRepository) {
    super(executor, mainThread);
    this.userFlowRepository = userFlowRepository;
  }

  @Override public void execute(UserFlow.Type type, List<UserFlow> userFlows,
      DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.type = type;
    this.userFlows = userFlows;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    List<UserFlow> userFlowsFromRepository = userFlowRepository.getUserFlow(type);

    for (UserFlow userFlowFromRepository : userFlowsFromRepository) {
      for (UserFlow userFlowDisplayed : userFlows) {
        if (userFlowFromRepository.getPhase() == userFlowDisplayed.getPhase()) {
          userFlowFromRepository.setIsDisplayed(true);
        }
      }
    }

    userFlowRepository.update(type, userFlowsFromRepository);
    performSuccessCallback(callback, true);
  }
}
