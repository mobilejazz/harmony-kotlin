package com.worldreader.core.datasource.network.datasource.collection;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.datasource.model.CollectionEntity;

import java.util.*;

public interface CollectionNetworkDataSource {

  void fetchCollections(CompletionCallback<List<CollectionEntity>> callback);

  void fetchCollection(int collectionId, CompletionCallback<CollectionEntity> callback);
}
