package com.worldreader.core.domain.interactors.userflow;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.interactors.application.GetSessionsInteractor;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor;
import com.worldreader.core.domain.model.UserFlow;
import com.worldreader.core.domain.repository.UserFlowRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetMyLibraryUserFlowToDisplayInteractorImpl extends BaseUserFlowInteractor
    implements GetMyLibraryUserFlowToDisplayInteractor {

  private final UserFlowRepository userFlowRepository;
  private final GetSessionsInteractor getSessionsInteractor;
  private final IsAnonymousUserInteractor isAnonymousUserInteractor;
  private final InteractorHandler interactorHandler;

  private DomainCallback<List<UserFlow>, ErrorCore<?>> callback;

  @Inject public GetMyLibraryUserFlowToDisplayInteractorImpl(InteractorExecutor executor,
      MainThread mainThread, UserFlowRepository userFlowRepository,
      GetSessionsInteractor getSessionsInteractor,
      final IsAnonymousUserInteractor isAnonymousUserInteractor,
      final InteractorHandler interactorHandler) {
    super(executor, mainThread);
    this.userFlowRepository = userFlowRepository;
    this.getSessionsInteractor = getSessionsInteractor;
    this.isAnonymousUserInteractor = isAnonymousUserInteractor;
    this.interactorHandler = interactorHandler;
  }

  @Override public void execute(DomainCallback<List<UserFlow>, ErrorCore<?>> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    final List<UserFlow> userFlows = userFlowRepository.getUserFlow(UserFlow.Type.MY_LIBRARY);

    getSessionsInteractor.execute(new DomainBackgroundCallback<List<Date>, ErrorCore<?>>() {
      @Override public void onSuccess(final List<Date> sessions) {
        final ListenableFuture<IsAnonymousUserInteractor.Type> isAnonymousUserLf =
            isAnonymousUserInteractor.execute(MoreExecutors.directExecutor());
        interactorHandler.addCallback(isAnonymousUserLf,
            new FutureCallback<IsAnonymousUserInteractor.Type>() {
              @Override
              public void onSuccess(final IsAnonymousUserInteractor.Type result) {
                List<UserFlow> userFlowToDisplay = new ArrayList<>();

                // Assign the number of session that should display
                int numberOfSessionToDisplay =
                    result == IsAnonymousUserInteractor.Type.REGISTERED ? 3 : 2;

                // The initial tutorial should be the first tutorial to display, we don't check the session.
                UserFlow homeUserFlow = getUserFlow(UserFlow.PHASE.MY_LIBRARY_HOME, userFlows);

                if (!homeUserFlow.isDisplayed()) {
                  userFlowToDisplay.add(homeUserFlow);
                } else {

                  // The next tutorials
                  UserFlow collectionsUserFlow =
                      getUserFlow(UserFlow.PHASE.MY_LIBRARY_COLLECTIONS, userFlows);
                  UserFlow categoriesUserFlow =
                      getUserFlow(UserFlow.PHASE.MY_LIBRARY_CATEGORIES, userFlows);

                  if (!collectionsUserFlow.isDisplayed()
                      && !categoriesUserFlow.isDisplayed()
                      && sessions.size() >= numberOfSessionToDisplay) {
                    userFlowToDisplay.add(collectionsUserFlow);
                    userFlowToDisplay.add(categoriesUserFlow);
                  }
                }

                performSuccessCallback(callback, userFlowToDisplay);
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
}
