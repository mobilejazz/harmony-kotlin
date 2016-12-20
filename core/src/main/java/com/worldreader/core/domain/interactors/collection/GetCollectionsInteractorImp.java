package com.worldreader.core.domain.interactors.collection;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.Collection;
import com.worldreader.core.domain.repository.CollectionRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class GetCollectionsInteractorImp extends AbstractInteractor<List<Collection>, ErrorCore>
    implements GetCollectionsInteractor {

  private CollectionRepository repository;

  private DomainCallback<List<Collection>, ErrorCore> callback;

  @Inject public GetCollectionsInteractorImp(InteractorExecutor executor, MainThread mainThread,
      CollectionRepository repository) {
    super(executor, mainThread);
    this.repository = repository;
  }

  @Override public void execute(DomainCallback<List<Collection>, ErrorCore> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    repository.collections(new CompletionCallback<List<Collection>>() {
      @Override public void onSuccess(List<Collection> result) {
        performSuccessCallback(callback, result);
      }

      @Override public void onError(ErrorCore error) {
        performErrorCallback(callback, error);
      }
    });
  }
}
