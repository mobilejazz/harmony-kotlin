package com.worldreader.core.domain.interactors.collection;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.domain.model.Collection;

import java.util.*;

public interface GetCollectionInteractor {

  void execute(int collectionId, CompletionCallback<Collection> callback);

  void execute(List<Integer> collectionsId, CompletionCallback<List<Collection>> callback);
}
