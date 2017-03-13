package com.worldreader.core.domain.interactors.userflow;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.UserFlow;

import java.util.List;

public interface UpdateStatusUserFlowInteractor {

  void execute(UserFlow.Type type, List<UserFlow> userFlows, DomainCallback<Boolean, ErrorCore<?>> callback);
}
