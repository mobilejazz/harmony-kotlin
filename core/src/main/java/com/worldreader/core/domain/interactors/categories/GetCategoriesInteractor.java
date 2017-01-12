package com.worldreader.core.domain.interactors.categories;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Category;

import java.util.*;

public interface GetCategoriesInteractor {

  void execute(DomainCallback<List<Category>, ErrorCore> callback);

  ListenableFuture<List<Category>> execute();
}
