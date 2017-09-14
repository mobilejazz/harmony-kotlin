package com.worldreader.core.domain.interactors.categories;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.model.Category;
import com.worldreader.core.domain.repository.CategoryRepository;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

@PerActivity public class GetCategoriesInteractor {

  private final ListeningExecutorService executor;
  private final CategoryRepository repository;

  @Inject public GetCategoriesInteractor(final ListeningExecutorService executor,
                                         final CategoryRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  public ListenableFuture<List<Category>> execute() {
    return execute(this.executor);
  }

  public ListenableFuture<List<Category>> execute(Executor executor) {
    final SettableFuture<List<Category>> settableFuture = SettableFuture.create();

    executor.execute(new Runnable() {
      @Override public void run() {
        repository.categories(new CompletionCallback<List<Category>>() {
          @Override public void onSuccess(final List<Category> result) {
            settableFuture.set(result);
          }

          @Override public void onError(final ErrorCore error) {
            settableFuture.setException(error.getCause());
          }
        });
      }
    });

    return settableFuture;
  }


}
