package com.worldreader.core.domain.interactors.userflow;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.UserFlow;
import com.worldreader.core.domain.repository.UserFlowRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.List;

public class ResetTutorialUserFlowInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>>
    implements ResetTutorialUserFlowInteractor {

  private UserFlowRepository userFlowRepository;
  private DomainCallback<Boolean, ErrorCore<?>> callback;

  @Inject
  public ResetTutorialUserFlowInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      UserFlowRepository userFlowRepository) {
    super(executor, mainThread);
    this.userFlowRepository = userFlowRepository;
  }

  @Override public void execute(DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    List<UserFlow> readerUserFlow = userFlowRepository.getUserFlow(UserFlow.Type.READER);
    List<UserFlow> myLibraryUserFlow = userFlowRepository.getUserFlow(UserFlow.Type.MY_LIBRARY);

    resetReaderUserFlow(readerUserFlow);
    resetMyLibraryUserFlow(myLibraryUserFlow);

    userFlowRepository.update(UserFlow.Type.READER, readerUserFlow);
    userFlowRepository.update(UserFlow.Type.MY_LIBRARY, myLibraryUserFlow);

    performSuccessCallback(callback, true);
  }

  private void resetMyLibraryUserFlow(List<UserFlow> userFlows) {
    for (UserFlow userFlow : userFlows) {
      if (userFlow.getPhase() == UserFlow.PHASE.MY_LIBRARY_CATEGORIES
          || userFlow.getPhase() == UserFlow.PHASE.MY_LIBRARY_HOME
          || userFlow.getPhase() == UserFlow.PHASE.MY_LIBRARY_COLLECTIONS) {
        // Reset the boolean that check if the userflow was displayed
        userFlow.setIsDisplayed(false);
      }
    }
  }

  private void resetReaderUserFlow(List<UserFlow> userFlows) {
    for (UserFlow userFlow : userFlows) {
      if (userFlow.getPhase() == UserFlow.PHASE.READER_READY_TO_READ
          || userFlow.getPhase() == UserFlow.PHASE.READER_READING_OPTIONS
          || userFlow.getPhase() == UserFlow.PHASE.READER_SPECIFIC_PAGE) {
        // Reset the boolean that check if the userflow was displayed
        userFlow.setIsDisplayed(false);
      }
    }
  }
}
