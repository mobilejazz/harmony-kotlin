package com.worldreader.core.domain.interactors.categories;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.Category;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

public class CategoriesMergerInteractorImpl extends AbstractInteractor<List<Category>, ErrorCore> implements CategoriesMergerInteractor {

  private final ListeningExecutorService executorService;

  private List<Category> categories;
  private DomainCallback<List<Category>, ErrorCore> callback;

  @Inject public CategoriesMergerInteractorImpl(MainThread mainThread, InteractorExecutor interactorExecutor, ListeningExecutorService executorService) {
    super(interactorExecutor, mainThread);
    this.executorService = executorService;
  }

  @Override public void execute(List<Category> categories, DomainCallback<List<Category>, ErrorCore> callback) {
    this.categories = categories;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<List<Category>> execute(final List<Category> categories) {
    return executorService.submit(new Callable<List<Category>>() {
      @Override public List<Category> call() throws Exception {
        return flatten(categories);
      }
    });
  }

  @Override public void run() {
    final List<Category> mergedCategories = flatten(this.categories);
    performSuccessCallback(callback, mergedCategories);
    callback = null;
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private List<Category> flatten(List<Category> categories) {
    if (categories == null) {
      return new ArrayList<>();
    }

    if (categories.size() == 0) {
      return categories;
    }

    // As we don't now if the original categories list has a lot of subcategories, at least we know
    // if the list has size > 10 (which is the original size that java assign to an ArrayList)
    // and if so, we assign larger capacity to avoid creating innecesaries instances
    List<Category> result = new ArrayList<>(categories.size() > 10 ? categories.size() + 10 : 10);

    for (Category category : categories) {
      List<Category> subCategories = category.getSubCategories();
      if (subCategories != null && subCategories.size() > 0) {
        // First add current category
        result.add(category);

        // Then modify each subcategory to add the title
        for (Category subcategory : subCategories) {
          subcategory.setTitle(category.getTitle() + " - " + subcategory.getTitle());
        }

        // And add it to the list
        result.addAll(subCategories);
      } else {
        // Simply add the category to the list
        result.add(category);
      }
    }

    return result;
  }

}
