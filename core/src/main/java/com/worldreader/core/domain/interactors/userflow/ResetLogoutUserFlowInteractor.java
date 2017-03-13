package com.worldreader.core.domain.interactors.userflow;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;

public interface ResetLogoutUserFlowInteractor {

  void execute(DomainBackgroundCallback<Boolean, ErrorCore<?>> callback);
}
