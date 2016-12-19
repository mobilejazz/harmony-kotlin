package com.worldreader.core.domain.repository;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.domain.model.Collection;

import java.util.*;

public interface CollectionRepository {

  void collections(CompletionCallback<List<Collection>> collections);

  void collection(int collectionId, CompletionCallback<Collection> collection);
}
