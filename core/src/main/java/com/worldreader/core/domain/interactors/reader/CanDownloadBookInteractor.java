package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;

public interface CanDownloadBookInteractor {

  void execute(DomainCallback<Boolean, ErrorCore<?>> callback);
}
