package com.worldreader.core.domain.interactors.application;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;

import java.util.*;

public interface GetSessionsInteractor {

  void execute(DomainCallback<List<Date>, ErrorCore<?>> callback);

  void execute(DomainBackgroundCallback<List<Date>, ErrorCore<?>> callback);
}
