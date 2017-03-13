package com.worldreader.core.userflow;

import android.content.Context;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.interactors.userflow.GetMyLibraryUserFlowToDisplayInteractor;
import com.worldreader.core.domain.interactors.userflow.GetReaderUserFlowToDisplayInteractor;
import com.worldreader.core.domain.interactors.userflow.UpdateStatusUserFlowInteractor;
import com.worldreader.core.domain.model.UserFlow;
import com.worldreader.core.domain.thread.MainThread;
import com.worldreader.core.userflow.model.TutorialModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class UserFlowTutorialManager implements UserFlowTutorial {

  private final GetMyLibraryUserFlowToDisplayInteractor getMyLibraryUserFlowToDisplayInteractor;
  private final GetReaderUserFlowToDisplayInteractor getReaderUserFlowToDisplayInteractor;
  private Context context;
  private MainThread mainThread;
  private UpdateStatusUserFlowInteractor updateStatusUserFlowInteractor;

  @Inject public UserFlowTutorialManager(Context context, MainThread mainThread,
      GetMyLibraryUserFlowToDisplayInteractor getMyLibraryUserFlowToDisplayInteractor,
      GetReaderUserFlowToDisplayInteractor getReaderUserFlowToDisplayInteractor,
      UpdateStatusUserFlowInteractor updateStatusUserFlowInteractor) {
    this.context = context;
    this.mainThread = mainThread;
    this.getMyLibraryUserFlowToDisplayInteractor = getMyLibraryUserFlowToDisplayInteractor;
    this.getReaderUserFlowToDisplayInteractor = getReaderUserFlowToDisplayInteractor;
    this.updateStatusUserFlowInteractor = updateStatusUserFlowInteractor;
  }

  @Override public void isCompleted(UserFlow.Type type, CompletionCallback<Boolean> callback) {

  }

  @Override public void get(UserFlow.Type type, CompletionCallback<List<TutorialModel>> callback) {
    switch (type) {
      case MY_LIBRARY:
        performMyLibraryUserFlow(callback);
        break;
      case READER:
        performReaderUserFlow(callback);
        break;
    }
  }

  private void performReaderUserFlow(final CompletionCallback<List<TutorialModel>> callback) {
    getReaderUserFlowToDisplayInteractor.execute(
        new DomainCallback<List<UserFlow>, ErrorCore<?>>(mainThread) {
          @Override public void onSuccessResult(List<UserFlow> userFlows) {
            List<TutorialModel> tutorialModels = new ArrayList<>(userFlows.size());

            for (UserFlow userFlow : userFlows) {
              if (userFlow.getPhase() == UserFlow.PHASE.READER_READY_TO_READ) {
                tutorialModels.add(TutorialModel.createInitialReaderTutorial(context));
              } else if (userFlow.getPhase() == UserFlow.PHASE.READER_SPECIFIC_PAGE) {
                tutorialModels.add(TutorialModel.createIndexReaderTutorial(context));
              } else if (userFlow.getPhase() == UserFlow.PHASE.READER_READING_OPTIONS) {
                tutorialModels.add(TutorialModel.createOptionsReaderTutorial(context));
              } else if (userFlow.getPhase() == UserFlow.PHASE.READER_SET_YOUR_GOALS) {
                tutorialModels.add(TutorialModel.createSetGoalsTutorial());
              } else if (userFlow.getPhase() == UserFlow.PHASE.READER_BECOME_A_WORLDREADER) {
                tutorialModels.add(TutorialModel.createBecomeWorldreaderTutorial());
              }
            }

            if (callback != null) {
              callback.onSuccess(tutorialModels);
            }

            updateStatusReaderUserFlow(userFlows);
          }

          @Override public void onErrorResult(ErrorCore<?> errorCore) {
            // Nothing to do
          }
        });
  }

  private void performMyLibraryUserFlow(final CompletionCallback<List<TutorialModel>> callback) {
    getMyLibraryUserFlowToDisplayInteractor.execute(
        new DomainCallback<List<UserFlow>, ErrorCore<?>>(mainThread) {
          @Override public void onSuccessResult(List<UserFlow> userFlows) {
            List<TutorialModel> tutorialModels = new ArrayList<>(userFlows.size());

            for (UserFlow userFlow : userFlows) {
              if (userFlow.getPhase() == UserFlow.PHASE.MY_LIBRARY_HOME) {
                tutorialModels.add(TutorialModel.createMyLibraryTutorial(context));
              } else if (userFlow.getPhase() == UserFlow.PHASE.MY_LIBRARY_COLLECTIONS) {
                tutorialModels.add(TutorialModel.createCollectionTutorial(context));
              } else if (userFlow.getPhase() == UserFlow.PHASE.MY_LIBRARY_CATEGORIES) {
                tutorialModels.add(TutorialModel.createCategoriesTutorial(context));
              }
            }

            if (callback != null) {
              callback.onSuccess(tutorialModels);
            }

            updateStatusMyLibraryUserFlow(userFlows);
          }

          @Override public void onErrorResult(ErrorCore<?> errorCore) {
            // Nothing to do
          }
        });
  }

  private void updateStatusReaderUserFlow(List<UserFlow> userFlows) {
    updateStatusUserFlowInteractor.execute(UserFlow.Type.READER, userFlows,
        new DomainCallback<Boolean, ErrorCore<?>>(mainThread) {
          @Override public void onSuccessResult(Boolean aBoolean) {
            // Nothing to do
          }

          @Override public void onErrorResult(ErrorCore<?> errorCore) {
            // Nothing to do
          }
        });
  }

  private void updateStatusMyLibraryUserFlow(List<UserFlow> userFlows) {
    updateStatusUserFlowInteractor.execute(UserFlow.Type.MY_LIBRARY, userFlows,
        new DomainCallback<Boolean, ErrorCore<?>>(mainThread) {
          @Override public void onSuccessResult(Boolean aBoolean) {
            // Nothing to do
          }

          @Override public void onErrorResult(ErrorCore<?> errorCore) {
            // Nothing to do
          }
        });
  }
}
