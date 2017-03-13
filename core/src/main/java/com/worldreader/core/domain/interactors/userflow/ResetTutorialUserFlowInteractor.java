package com.worldreader.core.domain.interactors.userflow;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;

public interface ResetTutorialUserFlowInteractor {

  void execute(DomainCallback<Boolean, ErrorCore<?>> callback);
}
