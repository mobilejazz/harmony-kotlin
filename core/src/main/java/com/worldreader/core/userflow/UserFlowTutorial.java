package com.worldreader.core.userflow;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.domain.model.UserFlow;
import com.worldreader.core.userflow.model.TutorialModel;

import java.util.List;

public interface UserFlowTutorial {

  void isCompleted(UserFlow.Type type, CompletionCallback<Boolean> callback);

  void get(UserFlow.Type type, CompletionCallback<List<TutorialModel>> callback);
}
