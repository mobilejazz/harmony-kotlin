package com.worldreader.core.domain.interactors.userflow;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.domain.model.UserFlow;
import com.worldreader.core.domain.repository.UserFlowRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

@Singleton
public class ResetLogoutUserFlowInteractor {

  private final ListeningExecutorService executor;
  private final UserFlowRepository repository;

  @Inject
  public ResetLogoutUserFlowInteractor(ListeningExecutorService executor, UserFlowRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<Void> execute() {
    return execute(executor);
  }

  public ListenableFuture<Void> execute(ListeningExecutorService executor) {
    return executor.submit(new Callable<Void>() {
      @Override public Void call() throws Exception {
        List<UserFlow> readerUserFlow = repository.getUserFlow(UserFlow.Type.READER);
        resetUserFlow(readerUserFlow);
        repository.update(UserFlow.Type.READER, readerUserFlow);
        return null;
      }
    });
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
