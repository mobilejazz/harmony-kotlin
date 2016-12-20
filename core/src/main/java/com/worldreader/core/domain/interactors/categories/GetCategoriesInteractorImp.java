package com.worldreader.core.domain.interactors.categories;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.Category;
import com.worldreader.core.domain.repository.CategoryRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class GetCategoriesInteractorImp extends AbstractInteractor<List<Category>, ErrorCore>
    implements GetCategoriesInteractor {

  private CategoryRepository repository;

  private DomainCallback<List<Category>, ErrorCore> callback;

  @Inject public GetCategoriesInteractorImp(InteractorExecutor executor, MainThread mainThread,
      CategoryRepository repository) {
    super(executor, mainThread);
    this.repository = repository;
  }

  @Override public void execute(DomainCallback<List<Category>, ErrorCore> callback) {
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    repository.categories(new CompletionCallback<List<Category>>() {
      @Override public void onSuccess(final List<Category> result) {
        performSuccessCallback(callback, result);
      }

      @Override public void onError(final ErrorCore error) {
        performErrorCallback(callback, error);
      }
    });
  }
}
