package com.worldreader.core.domain.interactors.userflow;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.interactors.application.GetSessionsInteractor;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor;
import com.worldreader.core.domain.model.UserFlow;
import com.worldreader.core.domain.repository.UserFlowRepository;
import com.worldreader.core.domain.thread.MainThread;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class GetReaderUserFlowToDisplayInteractorImpl extends BaseUserFlowInteractor
    implements GetReaderUserFlowToDisplayInteractor {

  private final UserFlowRepository userFlowRepository;
  private final GetSessionsInteractor getSessionsInteractor;
  private final IsAnonymousUserInteractor isAnonymousUserInteractor;
  private DomainCallback<List<UserFlow>, ErrorCore<?>> callback;

  @Inject public GetReaderUserFlowToDisplayInteractorImpl(InteractorExecutor executor,
      MainThread mainThread, UserFlowRepository userFlowRepository,
      GetSessionsInteractor getSessionsInteractor,
      final IsAnonymousUserInteractor isAnonymousUserInteractor) {
    super(executor, mainThread);
    this.userFlowRepository = userFlowRepository;
    this.getSessionsInteractor = getSessionsInteractor;
    this.isAnonymousUserInteractor = isAnonymousUserInteractor;
  }

  @Override public void execute(DomainCallback<List<UserFlow>, ErrorCore<?>> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    final List<UserFlow> userFlows = userFlowRepository.getUserFlow(UserFlow.Type.READER);

    getSessionsInteractor.execute(new DomainBackgroundCallback<List<Date>, ErrorCore<?>>() {
      @Override public void onSuccess(final List<Date> sessions) {
        final ListenableFuture<IsAnonymousUserInteractor.Type> isAnonymousUserLf =
            isAnonymousUserInteractor.execute(MoreExecutors.directExecutor());
        Futures.addCallback(isAnonymousUserLf,
            new FutureCallback<IsAnonymousUserInteractor.Type>() {
              @Override
              public void onSuccess(final IsAnonymousUserInteractor.Type result) {

                List<UserFlow> userFlowToResponse = new ArrayList<>();
                int sessionSize = sessions.size();

                // The initial tutorial should be the first tutorial to display, we don't check the session.
                UserFlow readyToReadUserFlow =
                    getUserFlow(UserFlow.PHASE.READER_READY_TO_READ, userFlows);

                if (!readyToReadUserFlow.isDisplayed()) {
                  userFlowToResponse.add(readyToReadUserFlow);
                } else {

                  // Calculating the session to display the Set your goals user flow
                  int sessionToDisplaySetGoals =
                      result == IsAnonymousUserInteractor.Type.REGISTERED ? 2 : 3;

                  UserFlow setGoalsUserFlow =
                      getUserFlow(UserFlow.PHASE.READER_SET_YOUR_GOALS, userFlows);

                  // The next user flow to display should the "Set your goals"
                  if (shouldDisplaySetGoals(sessionSize, sessionToDisplaySetGoals,
                      setGoalsUserFlow)) {
                    userFlowToResponse.add(setGoalsUserFlow);
                  } else {

                    // The next one is the "Become a Worldreader" only if the user is not registered yet
                    UserFlow becomeWorldReaderUserFlow =
                        getUserFlow(UserFlow.PHASE.READER_BECOME_A_WORLDREADER, userFlows);

                    if (shouldDisplayBecomeWorldreader(sessionSize, becomeWorldReaderUserFlow,
                        result == IsAnonymousUserInteractor.Type.ANONYMOUS)) {
                      userFlowToResponse.add(becomeWorldReaderUserFlow);
                    } else {

                      // The next one is the rest of the reader tutorials
                      UserFlow readingOptionsUserFlow =
                          getUserFlow(UserFlow.PHASE.READER_READING_OPTIONS, userFlows);
                      UserFlow specificPageUserFlow =
                          getUserFlow(UserFlow.PHASE.READER_SPECIFIC_PAGE, userFlows);

                      if (shouldDisplaySecondPhaseTutorial(sessionSize, readingOptionsUserFlow,
                          specificPageUserFlow)) {
                        userFlowToResponse.add(readingOptionsUserFlow);
                        userFlowToResponse.add(specificPageUserFlow);
                      }
                    }
                  }
                }

                performSuccessCallback(callback, userFlowToResponse);
              }

              @Override public void onFailure(final Throwable t) {
                performErrorCallback(callback, ErrorCore.of(t));
              }
            }, MoreExecutors.directExecutor());
      }

      @Override public void onError(ErrorCore<?> errorCore) {
        performErrorCallback(callback, errorCore);
      }
    });
  }

  private boolean shouldDisplaySetGoals(int sessionSize, int sessionToDisplaySetGoals,
      UserFlow setGoalsUserFlow) {
    return !setGoalsUserFlow.isDisplayed() && sessionSize >= sessionToDisplaySetGoals;
  }

  private boolean shouldDisplaySecondPhaseTutorial(int sessionSize, UserFlow readingOptionsUserFlow,
      UserFlow specificPageUserFlow) {
    return !readingOptionsUserFlow.isDisplayed()
        && !specificPageUserFlow.isDisplayed()
        && sessionSize >= 5;
  }

  private boolean shouldDisplayBecomeWorldreader(int sessionSize,
      UserFlow becomeWorldReaderUserFlow, boolean isAnonymous) {
    return !becomeWorldReaderUserFlow.isDisplayed() && isAnonymous && sessionSize >= 4;
  }
}
