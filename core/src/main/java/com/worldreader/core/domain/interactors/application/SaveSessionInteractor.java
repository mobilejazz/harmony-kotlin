package com.worldreader.core.domain.interactors.application;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;

import java.util.*;

public interface SaveSessionInteractor {

  void execute(Date date, DomainCallback<Boolean, ErrorCore<?>> callback);

  ListenableFuture<Boolean> execute(final Date date);
}
