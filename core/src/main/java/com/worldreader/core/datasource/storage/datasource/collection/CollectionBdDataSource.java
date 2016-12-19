package com.worldreader.core.datasource.storage.datasource.collection;

import com.worldreader.core.datasource.model.CollectionEntity;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

import java.util.*;

public interface CollectionBdDataSource {

  List<CollectionEntity> obtains(String key) throws InvalidCacheException;

  CollectionEntity obtain(String key) throws InvalidCacheException;

  void persist(String key, List<CollectionEntity> collectionEntities);

  void persist(String key, CollectionEntity collectionEntity);
}
